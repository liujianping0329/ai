package com.next.ai.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeService {

  private final String index;

  public KnowledgeService() throws IOException {
    this.index = read("knowledge-index");
  }

  public String getIndex() {
    return index;
  }

  public String load(List<String> documents) {

    if (documents == null || documents.isEmpty()) {
      return "";
    }

    return documents.stream()
        .distinct()
        .limit(2)
        .map(this::readUnchecked)
        .reduce("", (a, b) -> a + "\n\n" + b);
  }

  private String readUnchecked(String name) {
    try {
      return read(name);
    } catch (IOException e) {
      throw new RuntimeException(
          "Knowledge file load failed: " + name,
          e);
    }
  }

  private String read(String name) throws IOException {

    var resource = new ClassPathResource("ai/" + name + ".md");

    return new String(
        resource.getInputStream().readAllBytes(),
        StandardCharsets.UTF_8);
  }
}