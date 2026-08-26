package com.next.ai.vo.intro;

import io.swagger.v3.oas.annotations.media.Schema;

public record TableSelection(
        @Schema(description = "AI选择的数据库表名", example = "cost") String tableName,
        @Schema(description = "选择该表的理由", example = "用户查询的是消费记录") String reason) {
}