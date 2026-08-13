package com.jikku.backend.global.security;

import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * dev-login 보호키 검증. 이 엔드포인트는 아무 검증 없이 남의 계정 토큰을 내주므로 배포 환경에 그냥 열어둘 수 없다.
 * (카카오 로그인이 붙으면 엔드포인트째 삭제하는 것이 최종 목표)
 */
@Slf4j
@Component
public class DevLoginKeyVerifier {

    private final byte[] expectedKey;

    public DevLoginKeyVerifier(@Value("${dev-login.key:}") String key) {
        this.expectedKey = key.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 키가 설정되지 않았으면 통과가 아니라 거부한다(fail-closed).
     * 배포 시 환경변수를 빠뜨리는 것이 가장 흔한 실수인데, 그때 무방비로 열리는 쪽이 훨씬 위험하다.
     */
    public void verify(String providedKey) {
        if (expectedKey.length == 0) {
            log.error("dev-login 보호키가 설정되지 않아 요청을 거부한다. DEV_LOGIN_KEY 환경변수를 확인할 것");
            throw new BaseException(GeneralErrorCode.INVALID_DEV_LOGIN_KEY);
        }

        // 앞에서부터 다른 위치를 응답 시간으로 되짚을 수 없도록 상수 시간 비교를 쓴다
        if (providedKey == null
                || !MessageDigest.isEqual(expectedKey, providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new BaseException(GeneralErrorCode.INVALID_DEV_LOGIN_KEY);
        }
    }
}
