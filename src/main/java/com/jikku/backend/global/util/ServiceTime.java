package com.jikku.backend.global.util;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 서비스 기준 시각. 강원 여행 서비스라 사용자·데이터가 모두 한국 기준이므로 시간대를 못 박는다.
 * JVM 기본 시간대에 맡기면 배포 서버가 UTC일 때 날짜가 한국 시각 오전 9시에 바뀌어
 * "오늘의 추천"이 아침에 갑자기 갈리고, 축제 목록도 하루 늦게 만료된다.
 */
public final class ServiceTime {

    public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private ServiceTime() {
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }
}
