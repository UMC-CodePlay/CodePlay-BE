package umc.codeplay.service;

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
}
