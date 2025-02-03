package umc.codeplay.service;

import java.security.InvalidParameterException;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member joinMember(MemberRequestDTO.JoinDto request) {

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GeneralHandler(ErrorStatus.MEMBER_ALREADY_EXISTS);
        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
        return memberRepository.save(newMember);
    }

    public Member findOrCreateOAuthMember(String email, String name, SocialStatus socialStatus) {

        Member member = memberRepository.findByEmail(email).orElse(null);

        if (member == null) {
            member =
                    Member.builder()
                            .email(email)
                            .name(name)
                            .role(Role.USER)
                            .socialStatus(socialStatus)
                            .build();
            return memberRepository.save(member);
        } else if (member.getSocialStatus() != socialStatus) {
            throw new GeneralHandler(ErrorStatus.AUTHORIZATION_METHOD_ERROR);
        } else {
            return member;
        }
    }

    public SocialStatus getSocialStatus(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return SocialStatus.NONE;
        } else {
            return member.getSocialStatus();
        }
    }

    @Transactional
    public Member updateMember(String email, MemberRequestDTO.UpdateMemberDTO requestDto) {

        // MemberRepository의 findByEmail()을 사용하여 회원 조회
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));
        System.out.println("console log checking");
        // 사용자 입력 값
        String newPassword = requestDto.getNewPassword();
        String currentPassword = requestDto.getCurrentPassword();
        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidParameterException("기존 비밀번호가 일치하지 않습니다.");
            // 기존 비밀번호가 일치하고 새로운 비밀번호 입력값이 있을때 비밀번호 변경
        } else if (newPassword != null && !newPassword.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            member.setPassword(encodedPassword);
        } else {
            throw new InvalidParameterException("새로운 비밀번호를 입력해주세요.");
        }

        memberRepository.save(member);
        return member;
    }
}
