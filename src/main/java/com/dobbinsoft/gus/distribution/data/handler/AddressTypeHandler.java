package com.dobbinsoft.gus.distribution.data.handler;

import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import com.dobbinsoft.gus.distribution.data.properties.DistributionProperties;
import com.dobbinsoft.gus.distribution.data.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component // 若将Handler放入IoC中，则所有该类型的都会生效
@MappedTypes(OrderPO.Address.class)
public class AddressTypeHandler extends BaseTypeHandler<OrderPO.Address> {

    private static DistributionProperties distributionProperties;

    @Autowired
    public void setEcommerceProperties(DistributionProperties distributionProperties) {
        AddressTypeHandler.distributionProperties = distributionProperties;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, OrderPO.Address parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            try {
                String encryptedData = AESUtil.encrypt(parameter, distributionProperties.getAesKey());
                ps.setString(i, encryptedData);
            } catch (Exception e) {
                log.error("加密Address失败", e);
                throw new SQLException("加密Address失败", e);
            }
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public OrderPO.Address getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encryptedData = rs.getString(columnName);
        return decryptAddress(encryptedData);
    }

    @Override
    public OrderPO.Address getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encryptedData = rs.getString(columnIndex);
        return decryptAddress(encryptedData);
    }

    @Override
    public OrderPO.Address getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encryptedData = cs.getString(columnIndex);
        return decryptAddress(encryptedData);
    }

    private OrderPO.Address decryptAddress(String encryptedData) throws SQLException {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            return null;
        }
        
        try {
            return AESUtil.decrypt(encryptedData, distributionProperties.getAesKey(), OrderPO.Address.class);
        } catch (Exception e) {
            log.error("解密Address失败", e);
            throw new SQLException("解密Address失败", e);
        }
    }
} 