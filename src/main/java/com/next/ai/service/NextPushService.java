package com.next.ai.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.next.ai.vo.album.AlbumAnalyzeRequest;
import com.next.ai.vo.album.AlbumAnalyzeResult;

@Service
public class NextPushService {

  private final RestClient restClient;

  public NextPushService(RestClient.Builder builder, @Value("${app.nextjs.base-url}") String baseUrl) {

    this.restClient = builder
        .baseUrl(baseUrl)
        .build();
  }

  public void pushAlbum(AlbumAnalyzeRequest request, AlbumAnalyzeResult result) {

    Map<String, Object> detail = Map.of(
        "title", "图片动态提醒:" + result.title(),
        "content", result.detail(),
        "path", "user_func/album?id=" + request.albumId(),
        "imageUrl", request.imageUrl());

    restClient.post().uri("/api/external/v1/push").body(Map.of(
        "planetId", request.planetId(),
        "detail", detail)).retrieve().toBodilessEntity();
  }
}