package com.next.ai.vo;

public record ColumnMeta(
    String columnName,
    String dataType,
    String isNullable,
    String comment) {
}
