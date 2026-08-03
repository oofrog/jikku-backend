package com.jikku.backend.global.storage;

/**
 * 이미지 저장소 추상화. 스토리지 벤더가 바뀌어도 구현체만 갈아끼우면 되도록
 * 도메인/서비스는 SDK 타입을 모른 채 이 인터페이스에만 의존한다. (CLAUDE.md §6)
 */
public interface StoragePort {

    /**
     * 객체를 저장하고 공개 URL을 돌려준다.
     *
     * @param key         저장소 내 경로 (예: posts/12/202607/uuid.jpg)
     * @param content     파일 바이트. 업로드 용량을 서비스에서 제한하므로 스트림 대신 바이트로 받는다
     * @param contentType 브라우저가 이미지로 렌더링하도록 저장 시 함께 기록할 MIME 타입
     */
    String upload(String key, byte[] content, String contentType);

    /**
     * 업로드 때 받은 공개 URL로 객체를 삭제한다. 이 저장소가 발급하지 않은 URL은 무시한다.
     */
    void delete(String url);
}
