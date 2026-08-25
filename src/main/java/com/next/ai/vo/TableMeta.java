package com.next.ai.vo;

import java.util.List;

public record TableMeta(
    String name,
    String description,
    List<String> keywords) {
}
