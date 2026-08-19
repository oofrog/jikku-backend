package com.jikku.backend.domain.badge.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RegionBadge {
  CHUNCHEON(32010, "R001"),
  WONJU(32020, "R002"),
  GANGNEUNG(32030, "R003"),
  DONGHAE(32040, "R004"),
  TAEBAEK(32050, "R005"),
  SOKCHO(32060, "R006"),
  SAMCHEOK(32070, "R007"),
  HONGCHEON(32310, "R008"),
  HOENGSEONG(32320, "R009"),
  YEONGWOL(32330, "R010"),
  PYEONGCHANG(32340, "R011"),
  JEONGSEON(32350, "R012"),
  CHEORWON(32360, "R013"),
  HWACHEON(32370, "R014"),
  YANGGU(32380, "R015"),
  INJE(32390, "R016"),
  GOSEONG(32410, "R017"),
  YANGYANG(32420, "R018");

  private final Integer sigunguCd;
  private final String badgeNo;

  RegionBadge(Integer sigunguCd, String badgeNo) {
    this.sigunguCd = sigunguCd;
    this.badgeNo = badgeNo;
  }

  public String getBadgeNo() {
    return badgeNo;
  }

  public static Optional<RegionBadge> fromSigunguCd(Integer sigunguCd) {
    return Arrays.stream(values())
      .filter(regionBadge -> regionBadge.sigunguCd.equals(sigunguCd))
      .findFirst();
  }
}
