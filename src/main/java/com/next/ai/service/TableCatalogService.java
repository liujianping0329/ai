package com.next.ai.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import com.next.ai.vo.TableMeta;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class TableCatalogService {

  private final List<TableMeta> tables;

  public TableCatalogService(JsonMapper jsonMapper) throws IOException {

    var resource = new ClassPathResource("ai/table-catalog.json");

    this.tables = jsonMapper.readValue(
        resource.getInputStream(),
        new TypeReference<List<TableMeta>>() {
        });
  }

  public List<TableMeta> getTables() {
    return tables;
  }
}