package com.next.ai.vo.album;

import java.util.List;

public record AlbumAnalyzeItem(
    String name,
    List<String> alternativeNames,
    String estimatedAmount) {
}
