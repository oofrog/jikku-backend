package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.MapStickerResponse;
import com.jikku.backend.domain.map.repository.MapStickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MapStickerService {

  private final MapStickerRepository mapStickerRepository;

  @Transactional(readOnly = true)
  public FillMapListResponse<MapStickerResponse> getMapStickers(Long memberId, Integer sigunguCd) {
    return FillMapListResponse.from(
      mapStickerRepository.findByMemberIdAndSigunguCd(memberId, sigunguCd).stream()
        .map(MapStickerResponse::from)
        .toList()
    );
  }
}
