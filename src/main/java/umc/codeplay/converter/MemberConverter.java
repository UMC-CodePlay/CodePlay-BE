package umc.codeplay.converter;

import org.springframework.stereotype.Component;

import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;

@Component
public class MemberConverter {

    public static Member toMember(MemberRequestDTO.JoinDto request) {

        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .socialStatus(SocialStatus.NONE)
                .build();
    }

    public static MemberResponseDTO.JoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.JoinResultDTO.builder().id(member.getId()).build();
    }

    public static MemberResponseDTO.LoginResultDTO toLoginResultDTO(
            String email, String token, String refreshToken) {

        return MemberResponseDTO.LoginResultDTO.builder()
                .email(email)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public static MemberResponseDTO.UpdateResultDTO toUpdateResultDTO(Member member) {
        return MemberResponseDTO.UpdateResultDTO.builder()
                .email(member.getEmail())
                //                .profileUrl(member.getProfileUrl())
                .build();
    }
}
