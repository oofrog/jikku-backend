package com.jikku.backend.global.util;

/**
 * TourAPI overview(관광지·축제 설명) 가공.
 * 목록 응답에서만 쓴다 — 세부 조회는 원문을 그대로 내린다.
 */
public final class Overviews {

  private static final int SUMMARY_MAX_LENGTH = 30;

  private Overviews() {
  }

  /**
   * 카드용 한 줄 소개. 원문은 상세 설명이라 길고 &lt;br&gt; 같은 태그가 섞여 있어,
   * 그냥 30자를 자르면 태그가 중간에서 끊긴다. 태그를 걷어내고 공백을 정리한 뒤 자른다.
   */
  public static String summarize(String overview) {
    if (overview == null) {
      return null;
    }

    String plain = overview.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();

    return plain.length() <= SUMMARY_MAX_LENGTH
      ? plain
      : plain.substring(0, SUMMARY_MAX_LENGTH) + "…";
  }
}
