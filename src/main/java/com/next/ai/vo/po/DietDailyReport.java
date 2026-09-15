package com.next.ai.vo.po;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.next.ai.typehandler.UuidTypeHandler;

import lombok.Data;

@Data
@TableName(value = "diet_daily_report", autoResultMap = true)
public class DietDailyReport {

  @TableId(type = IdType.AUTO)
  private Long id;

  @TableField(value = "user_id", typeHandler = UuidTypeHandler.class)
  private UUID userId;

  @TableField("target_date")
  private LocalDate targetDate;

  private String status;

  private String summary;

  @TableField("breakfast_advice")
  private String breakfastAdvice;

  @TableField("lunch_advice")
  private String lunchAdvice;

  @TableField("dinner_advice")
  private String dinnerAdvice;

  @TableField("future_attention")
  private String futureAttention;

  @TableField("error_message")
  private String errorMessage;

  @TableField("completed_at")
  private OffsetDateTime completedAt;

  @TableField("report_push_at")
  private OffsetDateTime reportPushAt;
}
