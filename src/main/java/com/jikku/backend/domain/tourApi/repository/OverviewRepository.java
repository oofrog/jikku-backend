package com.jikku.backend.domain.tourApi.repository;

import java.util.List;

// spot·festival이 overview 적재에 대해 동일하게 동작하므로 적재 서비스가 둘을 같은 방식으로 다룬다.
public interface OverviewRepository {

    /**
     * 아직 overview를 받지 않은 대상을 방문자 적은 시군구부터 돌려준다.
     * 일 호출 한도가 있어 전량을 한 번에 못 채우므로, 추천에 먼저 노출될 지역을 앞세운다.
     */
    List<Long> findContentIdsWithoutOverview(int limit);

    void updateOverview(long contentId, String overview);

    int countWithoutOverview();
}
