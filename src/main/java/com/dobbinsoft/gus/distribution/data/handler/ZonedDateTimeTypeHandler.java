package com.dobbinsoft.gus.distribution.data.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * TypeHandler for ZonedDateTime. Stores as SQL TIMESTAMP in UTC (Instant),
 * reads back using system default ZoneId. Database TIMESTAMP does not retain zone info.
 */
@Component
@MappedTypes(ZonedDateTime.class)
public class ZonedDateTimeTypeHandler extends BaseTypeHandler<ZonedDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ZonedDateTime parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.TIMESTAMP);
        } else {
            // Use UTC calendar to avoid JDBC driver timezone shifts
            ps.setTimestamp(i, Timestamp.from(parameter.toInstant()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        }
    }

    @Override
    public ZonedDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        if (ts == null) return null;
        return ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public ZonedDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnIndex);
        if (ts == null) return null;
        return ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public ZonedDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp ts = cs.getTimestamp(columnIndex);
        if (ts == null) return null;
        return ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
    }
}

