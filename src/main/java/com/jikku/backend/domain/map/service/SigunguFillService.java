package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.SigunguFillRequest;
import com.jikku.backend.domain.map.dto.SigunguFillResponse;
import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.enums.FillType;
import com.jikku.backend.domain.map.enums.MapType;
import com.jikku.backend.domain.map.repository.FillMapRepository;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jikku.backend.domain.region.entity.Sigungu;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;

@Service
@RequiredArgsConstructor
public class SigunguFillService {

  private final FillMapRepository fillMapRepository;
  private final SigunguRepository sigunguRepository;

  @Transactional(readOnly = true)
  public FillMapListResponse<SigunguFillResponse> getSigunguFillMap(Long memberId) {
    return FillMapListResponse.from(
      fillMapRepository.findByMemberIdAndMapType(memberId, MapType.SIGUNGU)
        .stream()
        .map(SigunguFillResponse::from)
        .toList()
    );
  }

  @Transactional
  public SigunguFillResponse saveFillMap(SigunguFillRequest request, Long memberId) {
    Sigungu sigungu = getSigungu(request.sigunguCd());

    fillMapRepository.findByMemberIdAndMapTypeAndSigungu_SigunguCd(
      memberId,
      MapType.SIGUNGU,
      request.sigunguCd()
    ).ifPresent(fillMap -> {
      throw new BaseException(
        GeneralErrorCode.DUPLICATE_RESOURCE,
        "이미 해당 시군구 채우기 데이터가 존재합니다."
      );
    });

    FillMap saved = fillMapRepository.save(
      FillMap.ofSigungu(memberId, sigungu, request.fillType(), request.color(), request.imgUrl())
    );

    return SigunguFillResponse.from(saved);
  }

  @Transactional
  public SigunguFillResponse updateFillMap(Long fillMapId, SigunguFillRequest request, Long memberId) {
    FillMap fillMap = fillMapRepository.findById(fillMapId)
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "해당 지도 채우기 데이터가 존재하지 않습니다."
      ));

    if (fillMap.getMapType() != MapType.SIGUNGU) {
      throw new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "해당 지도 채우기 데이터가 존재하지 않습니다."
      );
    }

    if (!fillMap.getMemberId().equals(memberId)) {
      throw new BaseException(
        GeneralErrorCode.ACCESS_DENIED,
        "해당 지도 채우기 데이터를 수정할 권한이 없습니다."
      );
    }

    if (request.fillType() == FillType.COLOR) {
      fillMap.fillWithColor(request.color());
    }

    if (request.fillType() == FillType.IMAGE) {
      fillMap.fillWithImage(request.imgUrl());
    }

    FillMap updated = fillMapRepository.save(fillMap);
    return SigunguFillResponse.from(updated);
  }

  private Sigungu getSigungu(Integer sigunguCd) {
    return sigunguRepository.findBySigunguCd(sigunguCd)
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 시군구 코드입니다."
      ));
  }
}
