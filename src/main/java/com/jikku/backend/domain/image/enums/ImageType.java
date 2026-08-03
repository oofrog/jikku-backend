package com.jikku.backend.domain.image.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 업로드를 허용하는 이미지 형식.
 * 클라이언트가 보낸 Content-Type·파일명은 얼마든지 위조할 수 있어서,
 * 파일 선두 시그니처(매직 바이트)로 실제 형식을 판별한 뒤 그 결과만 신뢰한다.
 */
@Getter
@RequiredArgsConstructor
public enum ImageType {

    JPEG("image/jpeg", "jpg") {
        @Override
        protected boolean matches(byte[] content) {
            return hasSignature(content, 0, 0xFF, 0xD8, 0xFF);
        }
    },
    PNG("image/png", "png") {
        @Override
        protected boolean matches(byte[] content) {
            return hasSignature(content, 0, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
        }
    },
    WEBP("image/webp", "webp") {
        @Override
        protected boolean matches(byte[] content) {
            // RIFF 컨테이너 포맷이라 "RIFF" 다음 4바이트(파일 크기)를 건너뛴 자리에 "WEBP"가 온다
            return hasSignature(content, 0, 'R', 'I', 'F', 'F')
                    && hasSignature(content, 8, 'W', 'E', 'B', 'P');
        }
    };

    private final String contentType;
    private final String extension;

    protected abstract boolean matches(byte[] content);

    public static Optional<ImageType> detect(byte[] content) {
        return Arrays.stream(values())
                .filter(imageType -> imageType.matches(content))
                .findFirst();
    }

    protected static boolean hasSignature(byte[] content, int offset, int... signature) {
        if (content.length < offset + signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            // byte는 부호가 있어 0xFF 같은 값과 그냥 비교하면 안 맞는다
            if ((content[offset + i] & 0xFF) != signature[i]) {
                return false;
            }
        }

        return true;
    }
}
