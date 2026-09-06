package com.jikku.backend.domain.region.service;

import com.jikku.backend.domain.region.dto.EmdListResponse;
import com.jikku.backend.domain.region.dto.EmdResponse;
import com.jikku.backend.domain.region.repository.EmdRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmdService {

  private final EmdRepository emdRepository;

  @Transactional(readOnly = true)
  public EmdListResponse getEmds(Integer sigunguCd) {
    List<EmdResponse> content = emdRepository
      .findBySigungu_SigunguCd(sigunguCd)
      .stream()
      .map(EmdResponse::from)
      .toList();

    return EmdListResponse.from(content);
  }
}
