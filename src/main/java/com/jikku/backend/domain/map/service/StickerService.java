package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.StickerResponse;
import com.jikku.backend.domain.map.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StickerService {

  private final StickerRepository stickerRepository;

  @Transactional(readOnly = true)
  public FillMapListResponse<StickerResponse> getStickers() {
    return FillMapListResponse.from(
      stickerRepository.findAll().stream()
        .map(StickerResponse::from)
        .toList()
    );
  }
}
