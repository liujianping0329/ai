package com.next.ai.vo.album;

public record AlbumAnalyzeRequest(
        Long albumId,
        String imageUrl,
        String mimeType,
        Boolean isPush,
        Long planetId) {
}
