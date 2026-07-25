package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.MapStickerRequest;
import com.jikku.backend.domain.map.dto.MapStickerResponse;
import com.jikku.backend.domain.map.entity.MapSticker;
import com.jikku.backend.domain.map.entity.Sticker;
import com.jikku.backend.domain.map.repository.MapStickerRepository;
import com.jikku.backend.domain.map.repository.StickerRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MapStickerService {

  private final MapStickerRepository mapStickerRepository;
  private final StickerRepository stickerRepository;

  @Transactional(readOnly = true)
  public FillMapListResponse<MapStickerResponse> getMapStickers(Long memberId, Integer sigunguCd) {
    return FillMapListResponse.from(
      mapStickerRepository.findByMemberIdAndSigunguCd(memberId, sigunguCd).stream()
        .map(MapStickerResponse::from)
        .toList()
    );
  }

  @Transactional
  public MapStickerResponse saveMapSticker(Long memberId, Integer sigunguCd, MapStickerRequest request) {
    Sticker sticker = stickerRepository.findById(request.stickerId())
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 스티커입니다."
      ));

    MapSticker mapSticker = MapSticker.ofSticker(memberId, sigunguCd, sticker, request);
    MapSticker saved = mapStickerRepository.save(mapSticker);

    return MapStickerResponse.from(saved);
  }
}
