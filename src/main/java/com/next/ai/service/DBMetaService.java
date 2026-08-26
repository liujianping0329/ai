package com.next.ai.service;

import com.next.ai.mapper.DBMetaMapper;
import com.next.ai.vo.metaInfo.TableMetaInfo;

import org.springframework.stereotype.Service;

@Service
public class DBMetaService {

  private final DBMetaMapper schemaMapper;
  private final TableIntroService tableCatalogService;

  public DBMetaService(
      DBMetaMapper schemaMapper,
      TableIntroService tableCatalogService) {
    this.schemaMapper = schemaMapper;
    this.tableCatalogService = tableCatalogService;
  }

  public TableMetaInfo getTableSchema(String tableName) {

    boolean exists = tableCatalogService.getTables()
        .stream()
        .anyMatch(table -> table.name().equals(tableName));

    if (!exists) {
      throw new IllegalArgumentException(
          "不允许读取该表：" + tableName);
    }

    var columns = schemaMapper.getMeta(tableName);

    return new TableMetaInfo(
        tableName,
        columns);
  }
}