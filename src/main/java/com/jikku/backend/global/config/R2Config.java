package com.jikku.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Cloudflare R2 접속용 S3 클라이언트.
 */
@Configuration
public class R2Config {

    private static final String R2_ENDPOINT_FORMAT = "https://%s.r2.cloudflarestorage.com";

    @Bean
    public S3Client r2Client(
            @Value("${r2.account-id}") String accountId,
            @Value("${r2.access-key-id}") String accessKeyId,
            @Value("${r2.secret-access-key}") String secretAccessKey
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(R2_ENDPOINT_FORMAT.formatted(accountId)))
                // R2엔 리전 개념이 없지만 SDK가 값을 요구해서 auto로 채운다
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                // SDK 기본값은 모든 요청에 CRC 체크섬 헤더를 붙이는데 S3 호환 스토리지에서 거부되는 경우가 있어,
                // 프로토콜상 꼭 필요할 때만 계산하도록 낮춘다
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }
}
