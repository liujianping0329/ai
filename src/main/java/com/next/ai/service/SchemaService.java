package com.next.ai.service;

import com.next.ai.mapper.SchemaMapper;
import com.next.ai.vo.TableSchema;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

  private final SchemaMapper schemaMapper;
  private final TableCatalogService tableCatalogService;

  public SchemaService(
      SchemaMapper schemaMapper,
      TableCatalogService tableCatalogService) {
    this.schemaMapper = schemaMapper;
    this.tableCatalogService = tableCatalogService;
  }

  public TableSchema getTableSchema(String tableName) {

    boolean exists = tableCatalogService.getTables()
        .stream()
        .anyMatch(table -> table.name().equals(tableName));

    if (!exists) {
      throw new IllegalArgumentException(
          "不允许读取该表：" + tableName);
    }

    var columns = schemaMapper.getColumns(tableName);

    return new TableSchema(
        tableName,
        columns);
  }
}