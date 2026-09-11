package com.next.ai.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.next.ai.vo.po.Album;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
}