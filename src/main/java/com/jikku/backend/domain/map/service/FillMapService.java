package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.enums.MapType;
import com.jikku.backend.domain.map.repository.FillMapRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FillMapService {

  private final FillMapRepository fillMapRepository;

  // 시군구 색칠 지도 조회
  public List<FillMapResponse> getSigunguFillMap(Long memberId) {
    return fillMapRepository.findByMemberIdAndMapType(memberId, MapType.SIGUNGU)
      .stream()
      .map(fillMap -> new FillMapResponse(
        fillMap.getFillMapId(),
        fillMap.getSigunguId(),
        fillMap.getEmdId(),
        fillMap.getMapType().name(),
        fillMap.getFillType().name(),
        fillMap.getColor(),
        fillMap.getImgUrl()
      ))
      .toList();
  }

  // 읍면동 색칠 지도 조회
  public List<FillMapResponse> getEmdFillMap(Long memberId, Long sigunguId) {
    return fillMapRepository.findByMemberIdAndMapTypeAndSigunguId(memberId, MapType.EMD, sigunguId)
      .stream()
      .map(this::toResponse)
      .toList();
  }

  // 지도 채우기 저장
  public FillMapResponse saveFillMap(FillMapRequest request, Long memberId) {
    validateRequest(request);

    FillMap fillMap = FillMap.builder()
      .memberId(memberId)
      .sigunguId(request.sigunguId())
      .emdId(request.emdId())
      .mapType(request.mapType())
      .fillType(request.fillType())
      .color(request.color())
      .imgUrl(request.imgUrl())
      .build();

    FillMap saved = fillMapRepository.save(fillMap);
    return toResponse(saved);
  }

  public FillMapResponse updateFillMap(Long fillMapId, FillMapRequest request, Long memberId) {
    validateRequest(request);

    FillMap fillMap = fillMapRepository.findById(fillMapId)
      .orElseThrow(() -> new IllegalArgumentException("해당 지도 채우기 데이터가 존재하지 않습니다."));

    if (!fillMap.getMemberId().equals(memberId)) {
      throw new IllegalArgumentException("해당 지도 채우기 데이터를 수정할 권한이 없습니다.");
    }

    fillMap.updateFillMap(
      request.sigunguId(),
      request.emdId(),
      request.mapType(),
      request.fillType(),
      request.color(),
      request.imgUrl()
    );

    FillMap updated = fillMapRepository.save(fillMap);
    return toResponse(updated);
  }

  private void validateRequest(FillMapRequest request) {
    MapType mapType = request.mapType();

    if (mapType == MapType.SIGUNGU) {
      if (request.sigunguId() == null || request.emdId() != null) {
        throw new IllegalArgumentException("SIGUNGU 타입의 지역 정보가 올바르지 않습니다.");
      }
    }

    if (mapType == MapType.EMD) {
      if (request.sigunguId() == null || request.emdId() == null) {
        throw new IllegalArgumentException("EMD 타입의 지역 정보가 올바르지 않습니다.");
      }
    }
  }

  private FillMapResponse toResponse(FillMap fillMap) {
    return new FillMapResponse(
      fillMap.getFillMapId(),
      fillMap.getSigunguId(),
      fillMap.getEmdId(),
      fillMap.getMapType().name(),
      fillMap.getFillType().name(),
      fillMap.getColor(),
      fillMap.getImgUrl()
    );
  }
}
