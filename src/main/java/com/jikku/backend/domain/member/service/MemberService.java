package com.jikku.backend.domain.member.service;

import com.jikku.backend.domain.member.dto.MemberResponse;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 도메인 서비스. (조회/수정 등 회원 자체에 대한 로직)
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /** 마이페이지: 현재 로그인 회원(memberId)의 정보를 조회한다. */
    @Transactional(readOnly = true)
    public MemberResponse getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }
}
