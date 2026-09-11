package com.next.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.next.ai.service.AlbumAnalyzeTaskService;
import com.next.ai.vo.album.AlbumAnalyzeRequest;

@RestController
@RequestMapping("/api/ai/album")
public class AlbumAnalyzeController {

  private final AlbumAnalyzeTaskService taskService;

  public AlbumAnalyzeController(
      AlbumAnalyzeTaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/analyze")
  public ResponseEntity<Void> analyze(@RequestBody AlbumAnalyzeRequest request) {

    taskService.analyzeAsync(request);

    return ResponseEntity.accepted().build();
  }
}