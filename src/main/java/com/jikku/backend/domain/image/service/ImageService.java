package com.jikku.backend.domain.image.service;

import com.jikku.backend.domain.image.dto.ImageUploadResponse;
import com.jikku.backend.domain.image.enums.ImageType;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import com.jikku.backend.global.storage.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;
    private static final String KEY_PREFIX = "images";
    private static final DateTimeFormatter KEY_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");

    private final StoragePort storagePort;

    public ImageUploadResponse upload(Long memberId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException(GeneralErrorCode.MULTIPART_FILE_ERROR);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(GeneralErrorCode.FILE_SIZE_EXCEEDED);
        }

        byte[] content = readBytes(file);
        ImageType imageType = ImageType.detect(content)
                .orElseThrow(() -> new BaseException(GeneralErrorCode.INVALID_FILE_EXTENSION));

        String imgUrl = storagePort.upload(createKey(memberId, imageType), content, imageType.getContentType());
        return ImageUploadResponse.of(imgUrl);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BaseException(GeneralErrorCode.MULTIPART_FILE_ERROR);
        }
    }

    /**
     * 원본 파일명은 버리고 UUID로 새로 짓는다. 한글·중복·경로문자 문제를 한 번에 없애고 URL도 추측할 수 없게 된다.
     * 업로더·월별로 나눠두면 나중에 미사용 객체를 정리할 때 범위를 좁힐 수 있다.
     */
    private String createKey(Long memberId, ImageType imageType) {
        return "%s/%d/%s/%s.%s".formatted(
                KEY_PREFIX,
                memberId,
                LocalDate.now().format(KEY_MONTH_FORMAT),
                UUID.randomUUID(),
                imageType.getExtension()
        );
    }
}
