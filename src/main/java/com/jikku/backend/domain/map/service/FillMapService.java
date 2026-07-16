package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.enums.FillType;
import com.jikku.backend.domain.map.enums.MapType;
import com.jikku.backend.domain.map.repository.FillMapRepository;
import com.jikku.backend.domain.region.entity.Emd;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jikku.backend.domain.region.entity.Sigungu;
import com.jikku.backend.domain.region.repository.EmdRepository;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;

@Service
@RequiredArgsConstructor
public class FillMapService {

  private final FillMapRepository fillMapRepository;
  private final SigunguRepository sigunguRepository;
  private final EmdRepository emdRepository;

  // 시군구 색칠 지도 조회
  @Transactional(readOnly = true)
  public List<FillMapResponse> getSigunguFillMap(Long memberId) {
    return fillMapRepository.findByMemberIdAndMapType(memberId, MapType.SIGUNGU)
      .stream()
      .map(FillMapResponse::from)
      .toList();
  }

  // 읍면동 색칠 지도 조회
  @Transactional(readOnly = true)
  public List<FillMapResponse> getEmdFillMap(Long memberId, Integer sigunguCd) {
    Sigungu sigungu = sigunguRepository.findBySigunguCd(sigunguCd)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 시군구 코드입니다."));

    return fillMapRepository.findByMemberIdAndMapTypeAndSigungu_SigunguCd(memberId, MapType.EMD, sigungu.getSigunguCd())
      .stream()
      .map(FillMapResponse::from)
      .toList();
  }

  // 지도 채우기 저장
  @Transactional
  public FillMapResponse saveFillMap(FillMapRequest request, Long memberId) {
    validateRequest(request);
    Sigungu sigungu = getSigungu(request.sigunguCd());
    Emd emd = getEmd(request.emdId());

    FillMap fillMap = request.mapType() == MapType.SIGUNGU
      ? FillMap.ofSigungu(memberId, sigungu, request.fillType(), request.color(), request.imgUrl())
      : FillMap.ofEmd(memberId, sigungu, emd, request.fillType(), request.color(), request.imgUrl());

    FillMap saved = fillMapRepository.save(fillMap);
    return FillMapResponse.from(saved);
  }

  @Transactional
  public FillMapResponse updateFillMap(Long fillMapId, FillMapRequest request, Long memberId) {
    validateRequest(request);

    FillMap fillMap = fillMapRepository.findById(fillMapId)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "해당 지도 채우기 데이터가 존재하지 않습니다."));

    if (!fillMap.getMemberId().equals(memberId)) {
      throw new BaseException(GeneralErrorCode.ACCESS_DENIED, "해당 지도 채우기 데이터를 수정할 권한이 없습니다.");
    }
    Sigungu sigungu = getSigungu(request.sigunguCd());
    Emd emd = getEmd(request.emdId());

    fillMap.updateRegion(
      sigungu,
      emd,
      request.mapType()
    );

    if (request.fillType() == FillType.COLOR) {
      fillMap.fillWithColor(request.color());
    }

    if (request.fillType() == FillType.IMAGE) {
      fillMap.fillWithImage(request.imgUrl());
    }

    FillMap updated = fillMapRepository.save(fillMap);
    return FillMapResponse.from(updated);
  }

  private void validateRequest(FillMapRequest request) {

    MapType mapType = request.mapType();

    if (mapType == null) {
      throw new BaseException(GeneralErrorCode.INVALID_INPUT_VALUE, "mapType은 필수입니다.");
    }

    if (mapType == MapType.SIGUNGU) {
      if (request.sigunguCd() == null || request.emdId() != null) {
        throw new BaseException(GeneralErrorCode.INVALID_INPUT_VALUE, "SIGUNGU 타입의 지역 정보가 올바르지 않습니다.");
      }
    }

    if (mapType == MapType.EMD) {
      if (request.sigunguCd() == null || request.emdId() == null) {
        throw new BaseException(GeneralErrorCode.INVALID_INPUT_VALUE, "EMD 타입의 지역 정보가 올바르지 않습니다.");
      }
    }
  }

  private Sigungu getSigungu(Integer sigunguCd) {
    return sigunguRepository.findBySigunguCd(sigunguCd)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 시군구 코드입니다."));
  }

  private Emd getEmd(Long emdId) {
    if (emdId == null) {
      return null;
    }

    return emdRepository.findById(emdId)
      .orElseThrow(() -> new BaseException(GeneralErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 읍면동 ID입니다."));
  }
}
