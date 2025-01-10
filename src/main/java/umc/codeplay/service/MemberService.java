package umc.codeplay.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member joinMember(MemberRequestDTO.JoinDto request) {

    Member newMember = MemberConverter.toMember(request);
    newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
    return memberRepository.save(newMember);
  }
}
