package com.next.ai.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.next.ai.vo.album.AlbumAnalyzeRequest;
import com.next.ai.vo.album.AlbumAnalyzeResult;

@Service
public class AlbumAnalyzeTaskService {

  private final AlbumAnalyzeService albumAnalyzeService;
  private final AlbumAnalyzePersistenceService persistenceService;
  private final NextPushService nextPushService;

  public AlbumAnalyzeTaskService(
      AlbumAnalyzeService albumAnalyzeService,
      AlbumAnalyzePersistenceService persistenceService,
      NextPushService nextPushService) {

    this.albumAnalyzeService = albumAnalyzeService;
    this.persistenceService = persistenceService;
    this.nextPushService = nextPushService;
  }

  @Async
  public void analyzeAsync(AlbumAnalyzeRequest request) {

    try {
      AlbumAnalyzeResult result = albumAnalyzeService.analyze(
          request.imageUrl(),
          request.mimeType());

      persistenceService.save(request.albumId(), result);

      System.out.println(
          "Album AI分析完成: " + request.albumId());

      if (Boolean.TRUE.equals(request.isPush())) {
        try {
          System.out.println("开始调用Next推送");
          nextPushService.pushAlbum(request, result);
          System.out.println("Next推送完成");
        } catch (Exception e) {
          // 推送失败，不要把已经成功的AI分析也算成失败
          System.err.println(
              "Album 推送失败: "
                  + request.albumId()
                  + " / "
                  + e.getMessage());
        }
      }

    } catch (Exception e) {
      System.err.println(
          "Album AI分析失败: "
              + request.albumId()
              + " / "
              + e.getMessage());
      e.printStackTrace();
    }
  }
}
