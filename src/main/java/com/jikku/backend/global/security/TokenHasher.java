package com.jikku.backend.global.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * DB에 저장할 토큰을 해시한다.
 *
 * <p>비밀번호처럼 BCrypt(솔트+반복 해시)를 쓰지 않는 이유: 토큰은 서버가 만든 고엔트로피 값이라
 * 사전 공격 대상이 아니고, 매 재발급마다 대조해야 해서 느린 해시는 비용만 된다.
 * 같은 입력에 같은 출력이 나와야 컬럼 값과 바로 비교할 수 있다는 점도 BCrypt와 맞지 않는다.
 */
@Component
public class TokenHasher {

    private static final String ALGORITHM = "SHA-256";

    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM + "을 지원하지 않는 JVM이다", e);
        }
    }
}
