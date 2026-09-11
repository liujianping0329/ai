package com.next.ai.vo.album;

import java.util.List;

public record AlbumAnalyzeResult(
    String title,
    String detail,
    List<AlbumAnalyzeItem> items,
    List<AlbumAnalyzeMarker> markers) {
}
