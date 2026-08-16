package com.jikku.backend.domain.spot.dto;

import java.util.List;

/**
 * 목록 응답을 `content` 배열 하나로 감싸는 공용 래퍼.
 * 관광지·축제가 같은 형태를 쓰므로 제네릭으로 두고, 이름은 JSON 키(`content`)에 맞췄다.
 */
public record ContentListResponse<T>(List<T> content) {

    public static <T> ContentListResponse<T> from(List<T> content) {
        return new ContentListResponse<>(content);
    }
}
