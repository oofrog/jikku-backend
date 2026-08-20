package com.jikku.backend.domain.badge.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RegionBadge {
  CHUNCHEON(51110, "R001"),
  WONJU(51130, "R002"),
  GANGNEUNG(51150, "R003"),
  DONGHAE(51170, "R004"),
  TAEBAEK(51190, "R005"),
  SOKCHO(51210, "R006"),
  SAMCHEOK(51230, "R007"),
  HONGCHEON(51720, "R008"),
  HOENGSEONG(51730, "R009"),
  YEONGWOL(51750, "R010"),
  PYEONGCHANG(51760, "R011"),
  JEONGSEON(51770, "R012"),
  CHEORWON(51780, "R013"),
  HWACHEON(51790, "R014"),
  YANGGU(51800, "R015"),
  INJE(51810, "R016"),
  GOSEONG(51820, "R017"),
  YANGYANG(51830, "R018");

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
