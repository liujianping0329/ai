package com.next.ai.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class UuidTypeHandler extends BaseTypeHandler<UUID> {

  @Override
  public void setNonNullParameter(
      PreparedStatement statement,
      int parameterIndex,
      UUID parameter,
      JdbcType jdbcType) throws SQLException {
    statement.setObject(parameterIndex, parameter);
  }

  @Override
  public UUID getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
    return toUuid(resultSet.getObject(columnName));
  }

  @Override
  public UUID getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
    return toUuid(resultSet.getObject(columnIndex));
  }

  @Override
  public UUID getNullableResult(CallableStatement statement, int columnIndex) throws SQLException {
    return toUuid(statement.getObject(columnIndex));
  }

  private UUID toUuid(Object value) {
    return value == null ? null : UUID.fromString(value.toString());
  }
}
