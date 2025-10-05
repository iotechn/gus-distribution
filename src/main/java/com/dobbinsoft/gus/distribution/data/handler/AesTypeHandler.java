package com.dobbinsoft.gus.distribution.data.handler;

import com.dobbinsoft.gus.distribution.data.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class AesTypeHandler extends BaseTypeHandler<String> {

    private static final String AES_KEY = System.getenv("DISTRIBUTION_AES_KEY");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            try {
                String encryptedData = AESUtil.encrypt(parameter, AES_KEY);
                ps.setString(i, encryptedData);
            } catch (Exception e) {
                log.error("加密AppSecret失败", e);
                throw new SQLException("加密AppSecret失败", e);
            }
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encryptedData = rs.getString(columnName);
        return decryptAppSecret(encryptedData);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encryptedData = rs.getString(columnIndex);
        return decryptAppSecret(encryptedData);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encryptedData = cs.getString(columnIndex);
        return decryptAppSecret(encryptedData);
    }

    private String decryptAppSecret(String encryptedData) throws SQLException {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            return null;
        }
        
        try {
            return AESUtil.decrypt(encryptedData, AES_KEY);
        } catch (Exception e) {
            log.error("解密AppSecret失败", e);
            throw new SQLException("解密AppSecret失败", e);
        }
    }
} 