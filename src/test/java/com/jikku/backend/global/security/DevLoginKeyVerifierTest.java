package com.jikku.backend.global.security;

import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DevLoginKeyVerifierTest {

    private static final String KEY = "correct-dev-key";

    @Test
    @DisplayName("키가 일치하면 통과한다")
    void passesWithMatchingKey() {
        assertThatCode(() -> new DevLoginKeyVerifier(KEY).verify(KEY)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("실패 사유가 전용 코드로 나간다 (JWT 문제로 오해하지 않도록)")
    void failsWithDedicatedErrorCode() {
        assertThatThrownBy(() -> new DevLoginKeyVerifier(KEY).verify("wrong-key"))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(GeneralErrorCode.INVALID_DEV_LOGIN_KEY);
    }

    @Test
    @DisplayName("키가 다르거나 헤더가 없으면 거부한다")
    void rejectsWrongOrMissingKey() {
        DevLoginKeyVerifier verifier = new DevLoginKeyVerifier(KEY);

        assertThatThrownBy(() -> verifier.verify("wrong-key")).isInstanceOf(BaseException.class);
        assertThatThrownBy(() -> verifier.verify(null)).isInstanceOf(BaseException.class);
        assertThatThrownBy(() -> verifier.verify("")).isInstanceOf(BaseException.class);
        // 앞부분만 맞는 값도 통과하면 안 된다
        assertThatThrownBy(() -> verifier.verify("correct-dev-ke")).isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("서버에 키가 설정되지 않았으면 무엇을 보내도 거부한다 (fail-closed)")
    void rejectsEverythingWhenKeyIsUnset() {
        DevLoginKeyVerifier verifier = new DevLoginKeyVerifier("");

        assertThatThrownBy(() -> verifier.verify("")).isInstanceOf(BaseException.class);
        assertThatThrownBy(() -> verifier.verify(null)).isInstanceOf(BaseException.class);
        assertThatThrownBy(() -> verifier.verify("아무값")).isInstanceOf(BaseException.class);
    }
}
