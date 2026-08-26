package com.next.ai.vo.metaInfo;

public record ColumnMeta(
        String columnName,
        String dataType,
        String isNullable,
        String comment) {
}
