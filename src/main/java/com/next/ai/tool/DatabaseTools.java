package com.next.ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.next.ai.service.SchemaService;
import com.next.ai.vo.TableSchema;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTools {

  private final SchemaService schemaService;

  public DatabaseTools(
      SchemaService schemaService) {
    this.schemaService = schemaService;
  }

  @Tool(description = """
      获取指定数据库业务表的真实字段结构。
      当已经确定需要查询的业务表后，
      必须使用此工具获取字段信息，
      不允许自行猜测字段名。
      """)
  public TableSchema getTableSchema(@ToolParam(description = "数据库表名") String tableName) {
    return schemaService.getTableSchema(tableName);
  }
}
