package com.next.ai.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.next.ai.vo.intro.TableIntro;

@Service
public class TableIntroService {

  private final List<TableIntro> tables;

  public TableIntroService(JsonMapper jsonMapper) throws IOException {

    var resource = new ClassPathResource("ai/table-catalog.json");

    this.tables = jsonMapper.readValue(
        resource.getInputStream(),
        new TypeReference<List<TableIntro>>() {
        });
  }

  public List<TableIntro> getTables() {
    return tables;
  }
}