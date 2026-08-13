package com.jikku.backend.domain.tourApi.repository;

import java.util.List;

// spot·festival이 overview 적재에 대해 동일하게 동작하므로 적재 서비스가 둘을 같은 방식으로 다룬다.
public interface OverviewRepository {

    /**
     * 아직 overview를 받지 않은 대상을 우선순위가 높은 것부터 돌려준다.
     * 일 호출 한도가 있어 전량을 한 번에 못 채우므로 순서가 중요하다.
     * 무엇을 먼저 채울지는 구현마다 다르다 — 각 리포지토리의 쿼리 주석 참고.
     */
    List<Long> findContentIdsWithoutOverview(int limit);

    void updateOverview(long contentId, String overview);

    int countWithoutOverview();
}
