package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.badge.service.BadgeGrantService;
import com.jikku.backend.domain.mission.dto.MissionSpotListResponse;
import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.exception.MissionErrorCode;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.domain.spot.entity.Spot;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import com.jikku.backend.global.exception.BaseException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

  private static final Long MEMBER_ID = 1L;
  private static final Integer SIGUNGU_CD = 51110;

  @Mock
  MissionSpotRepository missionSpotRepository;

  @Mock
  SpotRepository spotRepository;

  @Mock
  SigunguRepository sigunguRepository;

  @Mock
  MissionAssignService missionAssignService;

  @Mock
  BadgeGrantService badgeGrantService;

  @InjectMocks
  MissionService missionService;

  @Test
  @DisplayName("이미 뽑아둔 미션이 있으면 새로 적재하지 않고, 인증한 미션은 목록에서 빠진다")
  void existingMissionsAreReusedWithoutCompletedOnes() {
    given(sigunguRepository.existsById(SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.existsByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(MEMBER_ID, SIGUNGU_CD))
      .willReturn(List.of(missionSpot(11L, 101L)));
    given(missionSpotRepository.countCompletedByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(1L);
    given(spotRepository.findAllById(anyCollection()))
      .willReturn(List.of(spot(101L, "설피마을")));

    MissionSpotListResponse response = missionService.getMissions(MEMBER_ID, SIGUNGU_CD);

    verify(missionAssignService, never()).assign(any(), any());
    assertThat(response.content()).hasSize(1);
    assertThat(response.completedCount()).isEqualTo(1);
    assertThat(response.content().get(0).title()).isEqualTo("설피마을");
  }

  @Test
  @DisplayName("전부 인증한 시군구를 다시 조회해도 미션을 새로 적재하지 않는다")
  void allCompletedDoesNotReassign() {
    given(sigunguRepository.existsById(SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.existsByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(MEMBER_ID, SIGUNGU_CD))
      .willReturn(List.of());
    given(missionSpotRepository.countCompletedByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(20L);

    MissionSpotListResponse response = missionService.getMissions(MEMBER_ID, SIGUNGU_CD);

    verify(missionAssignService, never()).assign(any(), any());
    assertThat(response.content()).isEmpty();
    assertThat(response.completedCount()).isEqualTo(20);
  }

  @Test
  @DisplayName("첫 조회면 적재한 미션을 그대로 응답한다")
  void firstCallAssignsMissions() {
    given(sigunguRepository.existsById(SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.existsByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(false);
    given(missionSpotRepository.findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(MEMBER_ID, SIGUNGU_CD))
      .willReturn(List.of(missionSpot(10L, 100L)));
    given(spotRepository.findAllById(anyCollection()))
      .willReturn(List.of(spot(100L, "오색약수")));

    MissionSpotListResponse response = missionService.getMissions(MEMBER_ID, SIGUNGU_CD);

    verify(missionAssignService).assign(MEMBER_ID, SIGUNGU_CD);
    assertThat(response.content()).hasSize(1);
    assertThat(response.completedCount()).isZero();
    assertThat(response.content().get(0).missionSpotId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("동시 요청으로 적재가 유니크 제약에 걸리면 먼저 들어간 미션을 읽어 응답한다")
  void concurrentAssignFallsBackToReread() {
    given(sigunguRepository.existsById(SIGUNGU_CD)).willReturn(true);
    given(missionSpotRepository.existsByMemberIdAndSigunguCd(MEMBER_ID, SIGUNGU_CD)).willReturn(false);
    willThrow(new DataIntegrityViolationException("duplicate key"))
      .given(missionAssignService).assign(MEMBER_ID, SIGUNGU_CD);
    given(missionSpotRepository.findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(MEMBER_ID, SIGUNGU_CD))
      .willReturn(List.of(missionSpot(20L, 200L)));
    given(spotRepository.findAllById(anyCollection()))
      .willReturn(List.of(spot(200L, "청평사")));

    MissionSpotListResponse response = missionService.getMissions(MEMBER_ID, SIGUNGU_CD);

    assertThat(response.content()).hasSize(1);
    assertThat(response.content().get(0).missionSpotId()).isEqualTo(20L);
  }

  @Test
  @DisplayName("없는 시군구 코드면 MISSION_006")
  void unknownSigunguThrows() {
    given(sigunguRepository.existsById(99999)).willReturn(false);

    assertThatThrownBy(() -> missionService.getMissions(MEMBER_ID, 99999))
      .isInstanceOf(BaseException.class)
      .extracting(e -> ((BaseException) e).getErrorCode())
      .isEqualTo(MissionErrorCode.MISSION_SIGUNGU_NOT_FOUND);

    verify(missionAssignService, never()).assign(any(), any());
  }

  private MissionSpot missionSpot(Long missionSpotId, Long contentId) {
    MissionSpot missionSpot = MissionSpot.of(MEMBER_ID, SIGUNGU_CD, contentId);
    ReflectionTestUtils.setField(missionSpot, "missionSpotId", missionSpotId);
    return missionSpot;
  }

  // Spot은 적재 전용 엔티티라 생성자가 protected다. 테스트에서만 리플렉션으로 채운다.
  private Spot spot(Long contentId, String title) {
    Spot spot = BeanUtils.instantiateClass(Spot.class);
    ReflectionTestUtils.setField(spot, "contentId", contentId);
    ReflectionTestUtils.setField(spot, "title", title);
    ReflectionTestUtils.setField(spot, "firstImage", "http://tong.visitkorea.or.kr/" + contentId + ".jpg");
    ReflectionTestUtils.setField(spot, "mapX", new BigDecimal("128.456000000000"));
    ReflectionTestUtils.setField(spot, "mapY", new BigDecimal("37.123000000000"));
    return spot;
  }
}
