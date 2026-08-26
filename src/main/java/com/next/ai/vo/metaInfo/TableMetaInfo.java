package com.next.ai.vo.metaInfo;

import java.util.List;

public record TableMetaInfo(
                String tableName,
                List<ColumnMeta> columns) {
}