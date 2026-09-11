package com.next.ai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.next.ai.mapper.AlbumItemMapper;
import com.next.ai.mapper.AlbumMapper;
import com.next.ai.vo.album.AlbumAnalyzeItem;
import com.next.ai.vo.album.AlbumAnalyzeMarker;
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
    Map<Integer, AlbumAnalyzeMarker> markersByItemIndex = indexMarkers(result.markers(), items.size());

    for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
      AlbumAnalyzeItem item = items.get(itemIndex);
      if (item == null || item.name() == null || item.name().isBlank()) {
        continue;
      }

      String name = item.name().trim();
      List<String> alternativeNames = normalizeAlternativeNames(item.alternativeNames(), name);
      Double centerXPercent = null;
      Double centerYPercent = null;
      AlbumAnalyzeMarker marker = markersByItemIndex.get(itemIndex);

      if (marker != null) {
        centerXPercent = normalizePercent(marker.centerXPercent());
        centerYPercent = normalizePercent(marker.centerYPercent());
      }

      AlbumItem albumItem = new AlbumItem();
      albumItem.setAlbumId(albumId);
      albumItem.setName(name);
      albumItem.setAlternativeNames(alternativeNames);
      albumItem.setEstimatedAmount(normalizeAmount(item.estimatedAmount()));
      albumItem.setCenterXPercent(centerXPercent);
      albumItem.setCenterYPercent(centerYPercent);

      albumItemMapper.insert(albumItem);
    }
  }

  private Map<Integer, AlbumAnalyzeMarker> indexMarkers(
      List<AlbumAnalyzeMarker> markers,
      int itemCount) {
    Map<Integer, AlbumAnalyzeMarker> markersByItemIndex = new HashMap<>();
    if (markers == null) {
      return markersByItemIndex;
    }

    for (AlbumAnalyzeMarker marker : markers) {
      if (markersByItemIndex.size() >= 4) {
        break;
      }

      if (marker == null || marker.itemIndex() < 0 || marker.itemIndex() >= itemCount) {
        continue;
      }

      markersByItemIndex.putIfAbsent(marker.itemIndex(), marker);
    }

    return markersByItemIndex;
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

  private double normalizePercent(double value) {
    if (!Double.isFinite(value)) {
      throw new IllegalArgumentException("Invalid item center percent");
    }

    return Math.max(0, Math.min(100, value));
  }

}
