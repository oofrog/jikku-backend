package com.jikku.backend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OverviewsTest {

  @Test
  @DisplayName("null은 그대로 null이다")
  void nullPassesThrough() {
    assertThat(Overviews.summarize(null)).isNull();
  }

  @Test
  @DisplayName("30자 이하면 자르지 않고 …도 붙이지 않는다")
  void shortOverviewIsKeptAsIs() {
    String thirtyChars = "가".repeat(30);

    assertThat(Overviews.summarize(thirtyChars)).isEqualTo(thirtyChars);
  }

  @Test
  @DisplayName("30자를 넘으면 30자까지 자르고 …를 붙인다")
  void longOverviewIsTruncated() {
    assertThat(Overviews.summarize("가".repeat(31))).isEqualTo("가".repeat(30) + "…");
  }

  @Test
  @DisplayName("HTML 태그를 걷어내고 공백을 정리한 뒤에 센다")
  void tagsAreStrippedBeforeCounting() {
    // 태그를 남긴 채 자르면 30자 안에서 태그가 끊긴다
    assertThat(Overviews.summarize("<br>가평에  있는 섬이다.<br />배를 타고 들어간다."))
      .isEqualTo("가평에 있는 섬이다. 배를 타고 들어간다.");
  }
}
