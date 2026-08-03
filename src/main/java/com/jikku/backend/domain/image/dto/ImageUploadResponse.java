package com.jikku.backend.domain.image.dto;

import lombok.Builder;

@Builder
public record ImageUploadResponse(
        String imgUrl
) {
    public static ImageUploadResponse of(String imgUrl) {
        return ImageUploadResponse.builder()
                .imgUrl(imgUrl)
                .build();
    }
}
