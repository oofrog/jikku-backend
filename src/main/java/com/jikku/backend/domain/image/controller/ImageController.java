package com.jikku.backend.domain.image.controller;

import com.jikku.backend.domain.image.dto.ImageUploadResponse;
import com.jikku.backend.domain.image.service.ImageService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "image", description = "이미지 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드",
            description = "이미지를 저장소에 올리고 공개 URL을 돌려준다. 여행 기록·지도 채우기가 이 URL을 받아 저장한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImageUploadResponse> upload(
            @AuthenticationPrincipal Long memberId,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.onSuccess(imageService.upload(memberId, file));
    }
}
