package com.jikku.backend.global.storage;

import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
public class R2StorageAdapter implements StoragePort {

    private final S3Client r2Client;
    private final String bucket;
    private final String publicBaseUrl;

    public R2StorageAdapter(
            S3Client r2Client,
            @Value("${r2.bucket}") String bucket,
            @Value("${r2.public-base-url}") String publicBaseUrl
    ) {
        this.r2Client = r2Client;
        this.bucket = bucket;
        // 설정값에 끝 슬래시가 있든 없든 URL이 같게 나오도록 여기서 한 번만 정규화한다
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
    }

    @Override
    public String upload(String key, byte[] content, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        try {
            r2Client.putObject(request, RequestBody.fromBytes(content));
        } catch (SdkException e) {
            log.error("R2 업로드 실패. key={}", key, e);
            throw new BaseException(GeneralErrorCode.FILE_UPLOAD_FAILED);
        }

        return publicBaseUrl + "/" + key;
    }

    @Override
    public void delete(String url) {
        String key = extractKey(url);
        if (key == null) {
            log.warn("R2가 발급한 URL이 아니라 삭제를 건너뛴다. url={}", url);
            return;
        }

        try {
            r2Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (SdkException e) {
            // 삭제 실패로 글 삭제 같은 본 작업까지 롤백시키지 않는다. 남은 객체는 저장비만 쓰고 조회되지 않는다
            log.error("R2 삭제 실패. key={}", key, e);
        }
    }

    private String extractKey(String url) {
        String prefix = publicBaseUrl + "/";
        if (url == null || !url.startsWith(prefix)) {
            return null;
        }

        String key = url.substring(prefix.length());
        return key.isBlank() ? null : key;
    }
}
