package com.next.ai.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.next.ai.mapper.AlbumMapper;
import com.next.ai.vo.album.AlbumAnalyzeRequest;
import com.next.ai.vo.album.AlbumAnalyzeResult;
import com.next.ai.vo.po.Album;

@Service
public class AlbumAnalyzeTaskService {

  private final AlbumAnalyzeService albumAnalyzeService;
  private final AlbumMapper albumMapper;
  private final NextPushService nextPushService;

  public AlbumAnalyzeTaskService(AlbumAnalyzeService albumAnalyzeService, AlbumMapper albumMapper,
      NextPushService nextPushService) {

    this.albumAnalyzeService = albumAnalyzeService;
    this.albumMapper = albumMapper;
    this.nextPushService = nextPushService;
  }

  @Async
  public void analyzeAsync(AlbumAnalyzeRequest request) {

    try {
      AlbumAnalyzeResult result = albumAnalyzeService.analyze(
          request.imageUrl(),
          request.mimeType());

      Album album = new Album();
      album.setId(request.albumId());
      album.setTitle(result.title());
      album.setDetail(result.detail());

      albumMapper.updateById(album);

      System.out.println(
          "Album AI分析完成: " + request.albumId());

      if (request.isPush()) {
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