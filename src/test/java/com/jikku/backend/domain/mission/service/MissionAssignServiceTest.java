package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MissionAssignServiceTest {

  private static final Integer SIGUNGU_CD = 51150;

  @Mock
  MissionSpotRepository missionSpotRepository;

  @Mock
  SpotRepository spotRepository;

  @InjectMocks
  MissionAssignService missionAssignService;

  @Captor
  ArgumentCaptor<List<MissionSpot>> savedCaptor;

  @Test
  @DisplayName("같은 회원·시군구는 몇 번을 뽑아도 같은 20개다")
  void sameMemberAndSigunguPicksSameSpots() {
    given(spotRepository.findContentIdsWithImageBySigungu(SIGUNGU_CD)).willReturn(candidates());

    missionAssignService.assign(1L, SIGUNGU_CD);
    missionAssignService.assign(1L, SIGUNGU_CD);

    List<List<Long>> picks = capturedContentIds(2);
    // 동시 첫 조회에서 두 요청이 다른 20개를 고르면 유니크 제약을 비껴가 40개가 저장된다
    assertThat(picks.get(0)).isEqualTo(picks.get(1));
    assertThat(picks.get(0)).hasSize(20).doesNotHaveDuplicates();
  }

  @Test
  @DisplayName("회원이 다르면 뽑히는 미션도 다르다")
  void differentMembersPickDifferentSpots() {
    given(spotRepository.findContentIdsWithImageBySigungu(SIGUNGU_CD)).willReturn(candidates());

    missionAssignService.assign(1L, SIGUNGU_CD);
    missionAssignService.assign(2L, SIGUNGU_CD);

    List<List<Long>> picks = capturedContentIds(2);
    assertThat(picks.get(0)).isNotEqualTo(picks.get(1));
  }

  @Test
  @DisplayName("후보가 20개보다 적으면 있는 만큼만 뽑는다")
  void fewerCandidatesThanMissionCount() {
    given(spotRepository.findContentIdsWithImageBySigungu(SIGUNGU_CD))
      .willReturn(LongStream.rangeClosed(1, 7).boxed().toList());

    missionAssignService.assign(1L, SIGUNGU_CD);

    assertThat(capturedContentIds(1).get(0)).hasSize(7);
  }

  private List<Long> candidates() {
    return LongStream.rangeClosed(1, 100).boxed().toList();
  }

  private List<List<Long>> capturedContentIds(int expectedCalls) {
    verify(missionSpotRepository, times(expectedCalls)).saveAll(savedCaptor.capture());
    return savedCaptor.getAllValues().stream()
      .map(saved -> saved.stream().map(MissionSpot::getContentId).toList())
      .toList();
  }
}
