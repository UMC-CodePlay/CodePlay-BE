package umc.codeplay.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.service.LikeService;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    public static Member toMember(MemberRequestDTO.JoinDto request) {

        return Member.builder()
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

    private final LikeService likeService; // LikeService 주입

    public MemberResponseDTO.GetMyHarmonyDTO toGetMyHarmonyDTO(Harmony harmony, Member member) {
        return MemberResponseDTO.GetMyHarmonyDTO.builder()
                .musicId(harmony.getMusic().getId())
                .musicTitle(harmony.getMusic().getTitle())
                .createdAt(harmony.getCreatedAt())
                .harmonyKey(harmony.getHarmonyKey())
                .bpm(harmony.getBpm())
                .soundPressure(harmony.getSoundPressure())
                .isLiked(
                        likeService.isLikedByUser(
                                member, harmony.getMusic())) // LikeService에서 좋아요 여부 확인
                .build();
    }
}
