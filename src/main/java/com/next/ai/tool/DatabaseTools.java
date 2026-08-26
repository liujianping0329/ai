package com.next.ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.next.ai.service.DBMetaService;
import com.next.ai.vo.metaInfo.TableMetaInfo;

import org.springframework.stereotype.Component;

@Component
public class DatabaseTools {

  private final DBMetaService dbMetaService;

  public DatabaseTools(
      DBMetaService dbMetaService) {
    this.dbMetaService = dbMetaService;
  }

  @Tool(description = """
      获取指定数据库业务表的真实字段结构。
      当已经确定需要查询的业务表后，
      必须使用此工具获取字段信息，
      不允许自行猜测字段名。
      """)
  public TableMetaInfo getTableSchema(@ToolParam(description = "数据库表名") String tableName) {
    return dbMetaService.getTableSchema(tableName);
  }
}
