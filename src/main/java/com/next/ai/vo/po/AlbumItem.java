package com.next.ai.vo.po;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.next.ai.typehandler.JsonbStringListTypeHandler;

import lombok.Data;

@Data
@TableName(value = "album_item", autoResultMap = true)
public class AlbumItem {

  @TableId(type = IdType.AUTO)
  private Long id;

  @TableField("album_id")
  private Long albumId;

  private String name;

  @TableField(
      value = "alternative_names",
      jdbcType = JdbcType.OTHER,
      typeHandler = JsonbStringListTypeHandler.class)
  private List<String> alternativeNames;

  @TableField("estimated_amount")
  private String estimatedAmount;

  @TableField("center_x_percent")
  private Double centerXPercent;

  @TableField("center_y_percent")
  private Double centerYPercent;

  @TableField("created_at")
  private OffsetDateTime createdAt;
}
