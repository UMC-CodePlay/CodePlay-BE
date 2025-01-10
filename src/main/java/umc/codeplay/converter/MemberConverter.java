package umc.codeplay.converter;

import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;

public class MemberConverter {

  public static Member toMember(MemberRequestDTO.JoinDto request) {

    return Member.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(request.getPassword())
        .build();
  }

  public static MemberResponseDTO.JoinResultDTO toJoinResultDTO(Member member) {
    return MemberResponseDTO.JoinResultDTO.builder().id(member.getId()).build();
  }
}
