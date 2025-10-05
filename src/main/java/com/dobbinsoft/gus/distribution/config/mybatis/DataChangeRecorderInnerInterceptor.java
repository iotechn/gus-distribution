/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dobbinsoft.gus.distribution.config.mybatis;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor 修改做了如下修改
 *
 * 1. 增加允许选择只拦截关注的表
 * 2. 增加允许选择只拦截关注的 INSERT / UPDATE / DELETE
 * 3. 强制要求复写处理事件
 * 4. 移除阈值判断
 * 5. 事件格式优化
 * 6. 修复枚举更新时，由于读出来的是String， 传入的是Enum，导致始终认为字段changed的问题
 * @author william.wei
 * @date 2025-8-26
 */
public abstract class DataChangeRecorderInnerInterceptor implements InnerInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, Set<String>> ignoredTableColumns = new ConcurrentHashMap<>();
    private final Set<String> ignoreAllColumns = new HashSet<>();//全部表的这些字段名，INSERT/UPDATE都忽略，delete暂时保留

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        final BoundSql boundSql = mpSh.boundSql();
        SqlCommandType sct = ms.getSqlCommandType();
        if ((sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) && focusOnMethods().contains(sct)) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            OperationResult operationResult;
            long startTs = System.currentTimeMillis();
            try {
                Statement statement = JsqlParserGlobal.parse(mpBs.sql());
                if (statement instanceof Insert) {
                    operationResult = processInsert((Insert) statement, mpSh.boundSql());
                } else if (statement instanceof Update) {
                    operationResult = processUpdate((Update) statement, ms, boundSql, connection);
                } else if (statement instanceof Delete) {
                    operationResult = processDelete((Delete) statement, ms, boundSql, connection);
                } else {
                    logger.info("other operation sql={}", mpBs.sql());
                    return;
                }
            } catch (Exception e) {
                logger.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), mpBs.sql(), e);
                return;
            }
            long costThis = System.currentTimeMillis() - startTs;
            if (operationResult != null) {
                operationResult.setCost(costThis);
                dealOperationResult(operationResult);
            }
        }
    }

    /**
     * 关注的事件，传最小值，以减少性能消耗
     * @return
     */
    protected abstract List<SqlCommandType> focusOnMethods();

    /**
     * 关注的表，传最小值，以减少性能消耗，传null | 空数组 表示全部表
     * @return
     */
    protected abstract List<String> focusOnTables();

    /**
     * 处理数据更新结果
     *
     * @param operationResult
     */
    protected abstract void dealOperationResult(OperationResult operationResult);

    public OperationResult processInsert(Insert insertStmt, BoundSql boundSql) {
        String operation = SqlCommandType.INSERT.name().toLowerCase();
        Table table = insertStmt.getTable();
        String tableName = table.getName();
        if (CollectionUtils.isNotEmpty(focusOnTables()) && !focusOnTables().contains(tableName)) {
            return null;
        }
        Optional<OperationResult> optionalOperationResult = ignoredTableColumns(tableName, operation);
        if (optionalOperationResult.isPresent()) {
            return optionalOperationResult.get();
        }
        OperationResult result = new OperationResult();
        result.setOperation(operation);
        result.setTableName(tableName);
        result.setRecordStatus(true);
        Map<String, Object> updatedColumnDatas = getUpdatedColumnDatas(tableName, boundSql, insertStmt);
        result.setChangedData(compareAndGetUpdatedColumnDatas(result.getTableName(), null, updatedColumnDatas));
        return result;
    }

    public OperationResult processUpdate(Update updateStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Expression where = updateStmt.getWhere();
        PlainSelect selectBody = new PlainSelect();
        Table table = updateStmt.getTable();
        String tableName = table.getName();
        if (CollectionUtils.isNotEmpty(focusOnTables()) && !focusOnTables().contains(tableName)) {
            return null;
        }
        String operation = SqlCommandType.UPDATE.name().toLowerCase();
        Optional<OperationResult> optionalOperationResult = ignoredTableColumns(tableName, operation);
        if (optionalOperationResult.isPresent()) {
            return optionalOperationResult.get();
        }
        selectBody.setFromItem(table);
        List<Column> updateColumns = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateColumns.addAll(updateSet.getColumns());
        }
        Columns2SelectItemsResult buildColumns2SelectItems = buildColumns2SelectItems(tableName, updateColumns);
        selectBody.setSelectItems(buildColumns2SelectItems.getSelectItems());
        selectBody.setWhere(where);
        SelectItem<PlainSelect> plainSelectSelectItem = new SelectItem<>(selectBody);

        BoundSql boundSql4Select = new BoundSql(mappedStatement.getConfiguration(), plainSelectSelectItem.toString(),
            prepareParameterMapping4Select(boundSql.getParameterMappings(), updateStmt),
            boundSql.getParameterObject());
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        Map<String, Object> additionalParameters = mpBoundSql.additionalParameters();
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            for (Map.Entry<String, Object> ety : additionalParameters.entrySet()) {
                boundSql4Select.setAdditionalParameter(ety.getKey(), ety.getValue());
            }
        }
        Map<String, Object> updatedColumnDatas = getUpdatedColumnDatas(tableName, boundSql, updateStmt);
        OriginalDataObj originalData = buildOriginalObjectData(updatedColumnDatas, selectBody, buildColumns2SelectItems.getPk(), mappedStatement, boundSql4Select, connection);
        OperationResult result = new OperationResult();
        result.setOperation(operation);
        result.setTableName(tableName);
        result.setRecordStatus(true);
        result.setChangedData(compareAndGetUpdatedColumnDatas(result.getTableName(), originalData, updatedColumnDatas));
        return result;
    }

    private Optional<OperationResult> ignoredTableColumns(String table, String operation) {
        final Set<String> ignoredColumns = ignoredTableColumns.get(table.toUpperCase());
        if (ignoredColumns != null) {
            if (ignoredColumns.stream().anyMatch("*"::equals)) {
                OperationResult result = new OperationResult();
                result.setOperation(operation);
                result.setTableName(table + ":*");
                result.setRecordStatus(false);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    private TableInfo getTableInfoByTableName(String tableName) {
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                return tableInfo;
            }
        }
        return null;
    }

    /**
     * 将update SET部分的jdbc参数去除
     *
     * @param originalMappingList 这里只会包含JdbcParameter参数
     * @param updateStmt
     * @return
     */
    private List<ParameterMapping> prepareParameterMapping4Select(List<ParameterMapping> originalMappingList, Update updateStmt) {
        List<Expression> updateValueExpressions = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateValueExpressions.addAll(updateSet.getValues());
        }
        int removeParamCount = 0;
        for (Expression expression : updateValueExpressions) {
            if (expression instanceof JdbcParameter) {
                ++removeParamCount;
            }
        }
        return originalMappingList.subList(removeParamCount, originalMappingList.size());
    }

    protected Map<String, Object> getUpdatedColumnDatas(String tableName, BoundSql updateSql, Statement statement) {
        Map<String, Object> columnNameValMap = new HashMap<>(updateSql.getParameterMappings().size());
        Map<Integer, String> columnSetIndexMap = new HashMap<>(updateSql.getParameterMappings().size());
        List<Column> selectItemsFromUpdateSql = new ArrayList<>();
        if (statement instanceof Update) {
            Update updateStmt = (Update) statement;
            int index = 0;
            for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
                selectItemsFromUpdateSql.addAll(updateSet.getColumns());
                final ExpressionList<Expression> updateList = (ExpressionList<Expression>) updateSet.getValues();
                for (int i = 0; i < updateList.size(); ++i) {
                    Expression updateExps = updateList.get(i);
                    if (!(updateExps instanceof JdbcParameter)) {
                        columnNameValMap.put(updateSet.getColumns().get(i).getColumnName().toUpperCase(), updateExps.toString());
                    }
                    columnSetIndexMap.put(index++, updateSet.getColumns().get(i).getColumnName().toUpperCase());
                }
            }
        } else if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            selectItemsFromUpdateSql.addAll(insert.getColumns());
            columnNameValMap.putAll(detectInsertColumnValuesNonJdbcParameters(insert));
        }
        Map<String, String> relatedColumnsUpperCaseWithoutUnderline = new HashMap<>(selectItemsFromUpdateSql.size(), 1);
        for (Column item : selectItemsFromUpdateSql) {
            //FIRSTNAME: FIRST_NAME/FIRST-NAME/FIRST$NAME/FIRST.NAME
            relatedColumnsUpperCaseWithoutUnderline.put(item.getColumnName().replaceAll("[._\\-$]", "").toUpperCase(), item.getColumnName().toUpperCase());
        }
        MetaObject metaObject = SystemMetaObject.forObject(updateSql.getParameterObject());
        int index = 0;
        for (ParameterMapping parameterMapping : updateSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                ++index;
                continue;
            }
            String[] arr = propertyName.split("\\.");
            String propertyNameTrim = arr[arr.length - 1].replace("_", "").toUpperCase();
            try {
                final String columnName = columnSetIndexMap.getOrDefault(index++, getColumnNameByProperty(propertyNameTrim, tableName));
                if (relatedColumnsUpperCaseWithoutUnderline.containsKey(propertyNameTrim)) {
                    final String colkey = relatedColumnsUpperCaseWithoutUnderline.get(propertyNameTrim);
                    Object valObj = metaObject.getValue(propertyName);
                    if (valObj instanceof IEnum) {
                        valObj = ((IEnum<?>) valObj).getValue();
                    } else if (valObj instanceof Enum) {
                        valObj = getEnumValue((Enum) valObj);
                    }
                    if (columnNameValMap.containsKey(colkey)) {
                        columnNameValMap.put(relatedColumnsUpperCaseWithoutUnderline.get(propertyNameTrim), String.valueOf(columnNameValMap.get(colkey)).replace("?", valObj == null ? "" : valObj.toString()));
                    }
                    if (columnName != null && !columnNameValMap.containsKey(columnName)) {
                        columnNameValMap.put(columnName, valObj);
                    }
                } else {
                    if (columnName != null) {
                        columnNameValMap.put(columnName, metaObject.getValue(propertyName));
                    }
                }
            } catch (Exception e) {
                logger.warn("get value error,propertyName:{},parameterMapping:{}", propertyName, parameterMapping);
            }
        }
        dealWithUpdateWrapper(columnSetIndexMap, columnNameValMap, updateSql);
        return columnNameValMap;
    }

    /**
     * @param originalDataObj
     * @return
     */
    private List<DataChangedRecord> compareAndGetUpdatedColumnDatas(String tableName, OriginalDataObj originalDataObj, Map<String, Object> columnNameValMap) {
        final Set<String> ignoredColumns = ignoredTableColumns.get(tableName.toUpperCase());
        if (originalDataObj == null || originalDataObj.isEmpty()) {
            DataChangedRecord oneRecord = new DataChangedRecord();
            List<DataColumnChangeResult> updateColumns = new ArrayList<>(columnNameValMap.size());
            for (Map.Entry<String, Object> ety : columnNameValMap.entrySet()) {
                String columnName = ety.getKey();
                if ((ignoredColumns == null || !ignoredColumns.contains(columnName)) && !ignoreAllColumns.contains(columnName)) {
                    updateColumns.add(DataColumnChangeResult.constrcutByUpdateVal(columnName, ety.getValue()));
                }
            }
            oneRecord.setUpdatedColumns(updateColumns);
//            oneRecord.setUpdatedColumns(Collections.EMPTY_LIST);
            return Collections.singletonList(oneRecord);
        }
        List<DataChangedRecord> originalDataList = originalDataObj.getOriginalDataObj();
        List<DataChangedRecord> updateDataList = new ArrayList<>(originalDataList.size());
        for (DataChangedRecord originalData : originalDataList) {
            if (originalData.hasUpdate(columnNameValMap, ignoredColumns, ignoreAllColumns)) {
                updateDataList.add(originalData);
            }
        }
        return updateDataList;
    }

    private Object getEnumValue(Enum enumVal) {
        Optional<String> enumValueFieldName = MybatisEnumTypeHandler.findEnumValueFieldName(enumVal.getClass());
        if (enumValueFieldName.isPresent()) {
            return SystemMetaObject.forObject(enumVal).getValue(enumValueFieldName.get());
        }
        return enumVal;

    }

    @SuppressWarnings("rawtypes")
    private void dealWithUpdateWrapper(Map<Integer, String> columnSetIndexMap, Map<String, Object> columnNameValMap, BoundSql updateSql) {
        if (columnSetIndexMap.size() <= columnNameValMap.size()) {
            return;
        }
        MetaObject mpgenVal = SystemMetaObject.forObject(updateSql.getParameterObject());
        if(!mpgenVal.hasGetter(Constants.WRAPPER)){
            return;
        }
        Object ew = mpgenVal.getValue(Constants.WRAPPER);
        if (ew instanceof UpdateWrapper || ew instanceof LambdaUpdateWrapper) {
            final String sqlSet = ew instanceof UpdateWrapper ? ((UpdateWrapper) ew).getSqlSet() : ((LambdaUpdateWrapper) ew).getSqlSet();// columnName=#{val}
            if (sqlSet == null) {
                return;
            }
            MetaObject ewMeta = SystemMetaObject.forObject(ew);
            final Map paramNameValuePairs = (Map) ewMeta.getValue("paramNameValuePairs");
            String[] setItems = sqlSet.split(",");
            for (String setItem : setItems) {
                //age=#{ew.paramNameValuePairs.MPGENVAL1}
                String[] nameAndValuePair = setItem.split("=", 2);
                if (nameAndValuePair.length == 2) {
                    String setColName = nameAndValuePair[0].trim().toUpperCase();
                    String setColVal = nameAndValuePair[1].trim();//#{.mp}
                    if (columnSetIndexMap.containsValue(setColName)) {
                        String[] mpGenKeyArray = setColVal.split("\\.");
                        String mpGenKey = mpGenKeyArray[mpGenKeyArray.length - 1].replace("}", "");
                        final Object setVal = paramNameValuePairs.get(mpGenKey);
                        if (setVal instanceof IEnum) {
                            columnNameValMap.put(setColName, String.valueOf(((IEnum<?>) setVal).getValue()));
                        } else {
                            columnNameValMap.put(setColName, setVal);
                        }
                    }
                }
            }
        }
    }

    private Map<String, String> detectInsertColumnValuesNonJdbcParameters(Insert insert) {
        Map<String, String> columnNameValMap = new HashMap<>(4);
        final Select select = insert.getSelect();
        List<Column> columns = insert.getColumns();
        if (select instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) select;
            final List<Select> selects = setOperationList.getSelects();
            if (CollectionUtils.isEmpty(selects)) {
                return columnNameValMap;
            }
            final Select selectBody = selects.get(0);
            if (!(selectBody instanceof Values)) {
                return columnNameValMap;
            }
            Values valuesStatement = (Values) selectBody;
            if (valuesStatement.getExpressions() instanceof ExpressionList) {
                ExpressionList expressionList = valuesStatement.getExpressions();
                List<Expression> expressions = expressionList;
                for (Expression expression : expressions) {
                    if (expression instanceof RowConstructor) {
                        final ExpressionList exprList = ((RowConstructor) expression);
                        final List<Expression> insertExpList = exprList;
                        for (int i = 0; i < insertExpList.size(); ++i) {
                            Expression e = insertExpList.get(i);
                            if (!(e instanceof JdbcParameter)) {
                                final String columnName = columns.get(i).getColumnName();
                                final String val = e.toString();
                                columnNameValMap.put(columnName, val);
                            }
                        }
                    }
                }
            }
        }
        return columnNameValMap;
    }

    private String getColumnNameByProperty(String propertyName, String tableName) {
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                final List<TableFieldInfo> fieldList = tableInfo.getFieldList();
                if (CollectionUtils.isEmpty(fieldList)) {
                    return propertyName;
                }
                for (TableFieldInfo tableFieldInfo : fieldList) {
                    if (propertyName.equalsIgnoreCase(tableFieldInfo.getProperty())) {
                        return tableFieldInfo.getColumn().toUpperCase();
                    }
                }
                return propertyName;
            }
        }
        return propertyName;
    }


    private String buildOriginalData(Select selectStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder sb = new StringBuilder("[");
            while (resultSet.next()) {
                sb.append("{");
                for (int i = 1; i <= columnCount; ++i) {
                    sb.append("\"").append(metaData.getColumnName(i)).append("\":\"");
                    Object res = resultSet.getObject(i);
                    if (res instanceof Clob) {
                        sb.append(DataColumnChangeResult.convertClob((Clob) res));
                    } else {
                        sb.append(res);
                    }
                    sb.append("\",");
                }
                sb.replace(sb.length() - 1, sb.length(), "}");
            }
            sb.append("]");
            resultSet.close();
            return sb.toString();
        } catch (Exception e) {
            logger.error("try to get record tobe deleted for selectStmt={}", selectStmt, e);
            return "failed to get original data";
        }
    }

    private OriginalDataObj buildOriginalObjectData(Map<String, Object> updatedColumnDatas, Select selectStmt, Column pk, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            List<DataChangedRecord> originalObjectDatas = new LinkedList<>();

            while (resultSet.next()) {
                originalObjectDatas.add(prepareOriginalDataObj(updatedColumnDatas, resultSet, pk));
            }
            OriginalDataObj result = new OriginalDataObj();
            result.setOriginalDataObj(originalObjectDatas);
            resultSet.close();
            return result;
        } catch (Exception e) {
            logger.error("try to get record tobe updated for selectStmt={}", selectStmt, e);
            return new OriginalDataObj();
        }
    }


    /**
     * get records : include related column with original data in DB
     *
     * @param resultSet
     * @param pk
     * @return
     * @throws SQLException
     */
    private DataChangedRecord prepareOriginalDataObj(Map<String, Object> updatedColumnDatas, ResultSet resultSet, Column pk) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<DataColumnChangeResult> originalColumnDatas = new LinkedList<>();
        DataColumnChangeResult pkval = null;
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toUpperCase();
            DataColumnChangeResult col;
            Object updateVal = updatedColumnDatas.get(columnName);
            if (updateVal != null && updateVal.getClass().getCanonicalName().startsWith("java.")) {
                col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i, updateVal.getClass()));
            } else {
                col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i));
            }
            if (pk != null && columnName.equalsIgnoreCase(pk.getColumnName())) {
                pkval = col;
            } else {
                originalColumnDatas.add(col);
            }
        }
        DataChangedRecord changedRecord = new DataChangedRecord();
        changedRecord.setOriginalColumnDatas(originalColumnDatas);
        if (pkval != null) {
            changedRecord.setPkColumnName(pkval.getColumnName());
            changedRecord.setPkColumnVal(pkval.getOriginalValue());
        }
        return changedRecord;
    }


    private Columns2SelectItemsResult buildColumns2SelectItems(String tableName, List<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return Columns2SelectItemsResult.build(Collections.singletonList(new SelectItem<>(new AllColumns())), 0);
        }
        List<SelectItem<?>> selectItems = new ArrayList<>(columns.size());
        for (Column column : columns) {
            selectItems.add(new SelectItem<>(column));
        }
        TableInfo tableInfo = getTableInfoByTableName(tableName);
        if (tableInfo == null || StringUtils.isBlank(tableInfo.getKeyColumn())) {
            return Columns2SelectItemsResult.build(selectItems, 0);
        }
        Column pk = new Column(tableInfo.getKeyColumn());
        selectItems.add(new SelectItem<>(pk));
        Columns2SelectItemsResult result = Columns2SelectItemsResult.build(selectItems, 1);
        result.setPk(pk);
        return result;
    }


    public OperationResult processDelete(Delete deleteStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Table table = deleteStmt.getTable();
        String tableName = table.getName();
        if (CollectionUtils.isNotEmpty(focusOnTables()) && !focusOnTables().contains(tableName)) {
            return null;
        }
        Expression where = deleteStmt.getWhere();
        // 删除前先按删除条件查询内容
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(table);
        selectBody.setSelectItems(Collections.singletonList(new SelectItem<>((new AllColumns()))));
        selectBody.setWhere(where);
        String originalData = buildOriginalData(selectBody, mappedStatement, boundSql, connection);
        OperationResult result = new OperationResult();
        result.setOperation("delete");
        result.setTableName(tableName);
        result.setRecordStatus(originalData.startsWith("["));
        result.setChangedData(null);
        return result;
    }

    /**
     * ignoredColumns = TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2; TABLE3.*; *.COLUMN1,COLUMN2
     * 多个表用分号分隔
     * TABLE_NAME1.COLUMN1,COLUMN2 : 表示忽略这个表的这2个字段
     * TABLE3.*: 表示忽略这张表的INSERT/UPDATE，delete暂时还保留
     * *.COLUMN1,COLUMN2:表示所有表的这个2个字段名都忽略
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

        String ignoredTableColumns = properties.getProperty("ignoredTableColumns");
        if (ignoredTableColumns == null || ignoredTableColumns.trim().isEmpty()) {
            return;
        }
        String[] array = ignoredTableColumns.split(";");
        for (String table : array) {
            int index = table.indexOf(".");
            if (index == -1) {
                logger.warn("invalid data={} for ignoredColumns, format should be TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2;", table);
                continue;
            }
            String tableName = table.substring(0, index).trim().toUpperCase();
            String[] columnArray = table.substring(index + 1).split(",");
            Set<String> columnSet = new HashSet<>(columnArray.length);
            for (String column : columnArray) {
                column = column.trim().toUpperCase();
                if (column.isEmpty()) {
                    continue;
                }
                columnSet.add(column);
            }
            if ("*".equals(tableName)) {
                ignoreAllColumns.addAll(columnSet);
            } else {
                this.ignoredTableColumns.put(tableName, columnSet);
            }
        }
    }

    @Data
    public static class OperationResult {

        private String operation;
        private boolean recordStatus;
        private String tableName;
        private List<DataChangedRecord> changedData;
        /**
         * cost for this plugin, ms
         */
        private long cost;


        @Override
        public String toString() {
            return "{" +
                "\"tableName\":\"" + tableName + "\"," +
                "\"operation\":\"" + operation + "\"," +
                "\"recordStatus\":\"" + recordStatus + "\"," +
                "\"changedData\":" + changedData + "," +
                "\"cost(ms)\":" + cost + "}";
        }
    }

    @Data
    public static class Columns2SelectItemsResult {

        private Column pk;
        /**
         * all column with additional columns: ID, etc.
         */
        private List<SelectItem<?>> selectItems;
        /**
         * newly added column count from meta data.
         */
        private int additionalItemCount;

        public static Columns2SelectItemsResult build(List<SelectItem<?>> selectItems, int additionalItemCount) {
            Columns2SelectItemsResult result = new Columns2SelectItemsResult();
            result.setSelectItems(selectItems);
            result.setAdditionalItemCount(additionalItemCount);
            return result;
        }
    }

    @Data
    public static class OriginalDataObj {

        private List<DataChangedRecord> originalDataObj;

        public boolean isEmpty() {
            return originalDataObj == null || originalDataObj.isEmpty();
        }

    }

    @Data
    public static class DataColumnChangeResult {

        private String columnName;
        private Object originalValue;
        private Object updateValue;

        @SuppressWarnings({"rawtypes", "unchecked"})
        public boolean isDataChanged(Object updateValue) {
            if (Objects.equals(originalValue, updateValue)) {
                return false;
            }
            if (originalValue instanceof Clob) {
                String originalStr = convertClob((Clob) originalValue);
                setOriginalValue(originalStr);
                return !originalStr.equals(updateValue);
            }
            if (originalValue instanceof Comparable) {
                Comparable original = (Comparable) originalValue;
                if (original.getClass().isInstance(updateValue)) {
                    Comparable update = (Comparable) updateValue;
                    return original.compareTo(update) != 0;
                }
            }
            if (updateValue.getClass().isEnum()) {
                // 如果是枚举，则以名称相比较
                String updateName = ((Enum<?>) updateValue).name();
                if (Objects.equals(originalValue, updateName)) {
                    return false;
                }
            }
            return true;
        }

        public static String convertClob(Clob clobObj) {
            try {
                return clobObj.getSubString(0, (int) clobObj.length());
            } catch (Exception e) {
                try (Reader is = clobObj.getCharacterStream()) {
                    char[] chars = new char[64];
                    int readChars;
                    StringBuilder sb = new StringBuilder();
                    while ((readChars = is.read(chars)) != -1) {
                        sb.append(chars, 0, readChars);
                    }
                    return sb.toString();
                } catch (Exception e2) {
                    //ignored
                    return "unknown clobObj";
                }
            }
        }

        public static DataColumnChangeResult constrcutByUpdateVal(String columnName, Object updateValue) {
            DataColumnChangeResult res = new DataColumnChangeResult();
            res.setColumnName(columnName);
            res.setUpdateValue(updateValue);
            return res;
        }

        public static DataColumnChangeResult constrcutByOriginalVal(String columnName, Object originalValue) {
            DataColumnChangeResult res = new DataColumnChangeResult();
            res.setColumnName(columnName);
            res.setOriginalValue(originalValue);
            return res;
        }

        public String generateDataStr() {
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(columnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(originalValue)).append("->").append(convertDoubleQuotes(updateValue)).append("\"").append(",");
            return sb.toString();
        }

        public String convertDoubleQuotes(Object obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString().replace("\"", "\\\"");
        }
    }

    @Data
    public static class DataChangedRecord {

        private String pkColumnName;
        private Object pkColumnVal;
        private List<DataColumnChangeResult> originalColumnDatas;
        private List<DataColumnChangeResult> updatedColumns;

        public boolean hasUpdate(Map<String, Object> columnNameValMap, Set<String> ignoredColumns, Set<String> ignoreAllColumns) {
            if (originalColumnDatas == null) {
                return true;
            }
            boolean hasUpdate = false;
            updatedColumns = new ArrayList<>(originalColumnDatas.size());
            for (DataColumnChangeResult originalColumn : originalColumnDatas) {
                final String columnName = originalColumn.getColumnName().toUpperCase();
                if (ignoredColumns != null && ignoredColumns.contains(columnName) || ignoreAllColumns.contains(columnName)) {
                    continue;
                }
                Object updatedValue = columnNameValMap.get(columnName);
                if (originalColumn.isDataChanged(updatedValue)) {
                    hasUpdate = true;
                    originalColumn.setUpdateValue(updatedValue);
                    updatedColumns.add(originalColumn);
                }
            }
            return hasUpdate;
        }

        public String generateUpdatedDataStr() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (pkColumnName != null) {
                sb.append("\"").append(pkColumnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(pkColumnVal)).append("\"").append(",");
            }
            for (DataColumnChangeResult update : updatedColumns) {
                sb.append(update.generateDataStr());
            }
            sb.replace(sb.length() - 1, sb.length(), "}");
            return sb.toString();
        }

        public String convertDoubleQuotes(Object obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString().replace("\"", "\\\"");
        }
    }
}
