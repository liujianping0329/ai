package com.next.ai.vo;

import java.util.List;

public record TableSchema(
    String tableName,
    List<ColumnMeta> columns) {
}