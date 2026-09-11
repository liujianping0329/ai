package com.next.ai.vo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("album")
public class Album {

  @TableId
  private Long id;

  private String title;

  private String detail;
}