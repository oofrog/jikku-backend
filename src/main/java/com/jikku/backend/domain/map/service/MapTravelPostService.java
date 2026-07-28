package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.MapTravelPostResponse;
import com.jikku.backend.domain.map.entity.MapSticker;
import com.jikku.backend.domain.map.enums.StickerType;
import com.jikku.backend.domain.map.repository.MapStickerRepository;
import com.jikku.backend.domain.travelPost.entity.TravelPost;
import com.jikku.backend.domain.travelPost.repository.TravelPostRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapTravelPostService {

  private final MapStickerRepository mapStickerRepository;
  private final TravelPostRepository travelPostRepository;

  public FillMapListResponse<MapTravelPostResponse> getMapTravelPosts(Long memberId, Integer sigunguCd) {
    return FillMapListResponse.from(
      mapStickerRepository.findByMemberIdAndSigunguCdAndStickerType(memberId, sigunguCd, StickerType.POST)
        .stream()
        .map(this::toResponse)
        .toList()
    );
  }

  private MapTravelPostResponse toResponse(MapSticker mapSticker) {
    TravelPost travelPost = travelPostRepository.findById(mapSticker.getTravelPostId())
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 여행 게시글입니다."
      ));

    return MapTravelPostResponse.builder()
      .mapStickerId(mapSticker.getMapStickerId())
      .stickerType(mapSticker.getStickerType().name())
      .travelPostId(travelPost.getTravelPostId())
      .firstImage(travelPost.getFirstImage())
      .title(travelPost.getTitle())
      .posX(mapSticker.getPosX())
      .posY(mapSticker.getPosY())
      .scale(mapSticker.getScale())
      .zIndex(mapSticker.getZIndex())
      .build();
  }
}
