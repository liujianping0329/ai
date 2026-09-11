package com.next.ai.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.next.ai.mapper.AlbumItemMapper;
import com.next.ai.mapper.AlbumMapper;
import com.next.ai.vo.album.AlbumAnalyzeItem;
import com.next.ai.vo.album.AlbumAnalyzeResult;
import com.next.ai.vo.po.Album;
import com.next.ai.vo.po.AlbumItem;

@Service
public class AlbumAnalyzePersistenceService {

  private final AlbumMapper albumMapper;
  private final AlbumItemMapper albumItemMapper;

  public AlbumAnalyzePersistenceService(
      AlbumMapper albumMapper,
      AlbumItemMapper albumItemMapper) {
    this.albumMapper = albumMapper;
    this.albumItemMapper = albumItemMapper;
  }

  @Transactional
  public void save(Long albumId, AlbumAnalyzeResult result) {
    Album album = new Album();
    album.setId(albumId);
    album.setTitle(result.title());
    album.setDetail(result.detail());

    if (albumMapper.updateById(album) != 1) {
      throw new IllegalArgumentException("Album 不存在: " + albumId);
    }

    // Re-analysis replaces old items so it cannot create duplicates or stale rows.
    albumItemMapper.deleteByMap(Map.of("album_id", albumId));

    List<AlbumAnalyzeItem> items = result.items() == null ? List.of() : result.items();
    for (AlbumAnalyzeItem item : items) {
      if (item == null || item.name() == null || item.name().isBlank()) {
        continue;
      }

      String name = item.name().trim();
      List<String> alternativeNames = normalizeAlternativeNames(item.alternativeNames(), name);

      AlbumItem albumItem = new AlbumItem();
      albumItem.setAlbumId(albumId);
      albumItem.setName(name);
      albumItem.setAlternativeNames(alternativeNames);
      albumItem.setEstimatedAmount(normalizeAmount(item.estimatedAmount()));

      albumItemMapper.insert(albumItem);
    }
  }

  private List<String> normalizeAlternativeNames(List<String> names, String primaryName) {
    if (names == null) {
      return List.of();
    }

    return names.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(name -> !name.isEmpty())
        .filter(name -> !name.equals(primaryName))
        .distinct()
        .limit(3)
        .toList();
  }

  private String normalizeAmount(String amount) {
    return amount == null || amount.isBlank() ? "份量不明" : amount.trim();
  }

}
