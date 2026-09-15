package com.next.ai.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.next.ai.vo.album.AlbumAnalyzeRequest;
import com.next.ai.vo.album.AlbumAnalyzeResult;
import com.next.ai.vo.diet.DietDailyReportRequest;
import com.next.ai.vo.diet.DietDailyReportResult;

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
        "path", "user_func/album/detail/" + request.albumId(),
        "imageUrl", request.imageUrl());

    restClient.post().uri("/api/external/v1/push").body(Map.of(
        "planetId", request.planetId(),
        "detail", detail)).retrieve().toBodilessEntity();
  }

  public void pushDailyReport(DietDailyReportRequest request, DietDailyReportResult result) {
    Map<String, Object> detail = Map.of(
        "title", "饮食日报已生成",
        "content", result.summary() == null ? "点击查看今日饮食建议" : result.summary(),
        "path", "user_func/album?tab=yesterday");

    restClient.post().uri("/api/external/v1/push").body(Map.of(
        "userId", request.userId().toString(),
        "detail", detail)).retrieve().toBodilessEntity();
  }
}
