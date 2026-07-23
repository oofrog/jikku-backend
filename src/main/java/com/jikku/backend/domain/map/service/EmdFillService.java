package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.EmdFillRequest;
import com.jikku.backend.domain.map.dto.EmdFillResponse;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.enums.MapType;
import com.jikku.backend.domain.map.repository.FillMapRepository;
import com.jikku.backend.domain.region.entity.Emd;
import com.jikku.backend.domain.region.entity.Sigungu;
import com.jikku.backend.domain.region.repository.EmdRepository;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmdFillService {

  private final FillMapRepository fillMapRepository;
  private final SigunguRepository sigunguRepository;
  private final EmdRepository emdRepository;

  @Transactional(readOnly = true)
  public FillMapListResponse<EmdFillResponse> getEmdFillMap(Long memberId, Integer sigunguCd) {
    Sigungu sigungu = sigunguRepository.findBySigunguCd(sigunguCd)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 시군구 코드입니다."));

    return FillMapListResponse.from(
      fillMapRepository.findByMemberIdAndMapTypeAndSigungu_SigunguCd(memberId, MapType.EMD, sigungu.getSigunguCd())
        .stream()
        .map(EmdFillResponse::from)
        .toList()
    );
  }

  @Transactional
  public EmdFillResponse saveEmdFillMap(Long memberId, Integer sigunguCd, EmdFillRequest request) {
    Sigungu sigungu = sigunguRepository.findBySigunguCd(sigunguCd)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 시군구 코드입니다."));

    Emd emd = getEmd(request.emdId());
    validateEmdBelongsToSigungu(sigungu, emd);

    fillMapRepository.findByMemberIdAndMapTypeAndSigungu_SigunguCdAndEmd_EmdId(
      memberId,
      MapType.EMD,
      sigunguCd,
      request.emdId()
    ).ifPresent(fillMap -> {
      throw new BaseException(
        GeneralErrorCode.DUPLICATE_RESOURCE,
        "이미 해당 읍면동 채우기 데이터가 존재합니다."
      );
    });

    FillMap saved = fillMapRepository.save(
      FillMap.ofEmd(memberId, sigungu, emd, request.fillType(), request.color(), request.imgUrl())
    );

    return EmdFillResponse.from(saved);
  }

  private Emd getEmd(Long emdId) {
    if (emdId == null) {
      return null;
    }

    return emdRepository.findById(emdId)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 읍면동 ID입니다."));
  }

  private void validateEmdBelongsToSigungu(Sigungu sigungu, Emd emd) {
    if (emd == null) {
      return;
    }

    if (!emd.getSigungu().getSigunguCd().equals(sigungu.getSigunguCd())) {
      throw new BaseException(GeneralErrorCode.INVALID_INPUT_VALUE, "선택한 읍면동이 해당 시군구에 속하지 않습니다.");
    }
  }
}
