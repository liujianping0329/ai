package com.next.ai.typehandler;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

public class JsonbStringListTypeHandler extends JacksonTypeHandler {

  public JsonbStringListTypeHandler(Class<?> type) {
    super(type);
  }

  public JsonbStringListTypeHandler(Class<?> type, Field field) {
    super(type, field);
  }

  @Override
  public void setNonNullParameter(
      PreparedStatement statement,
      int parameterIndex,
      Object parameter,
      JdbcType jdbcType) throws SQLException {
    statement.setObject(parameterIndex, toJson(parameter), Types.OTHER);
  }
}
