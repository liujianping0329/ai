package com.next.ai.vo.diet;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DietDailyReportRequest(
    UUID userId,
    LocalDate targetDate,
    List<Long> albumIds,
    List<Album> breakfastAlbums,
    List<Album> lunchAlbums,
    List<Album> dinnerAlbums) {

  public record Album(
      Long albumId,
      Integer participantCount,
      List<Item> items) {
  }

  public record Item(
      Long itemId,
      String name,
      String estimatedAmount) {
  }
}
