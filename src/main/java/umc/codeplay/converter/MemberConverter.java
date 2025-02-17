package umc.codeplay.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Task;
import umc.codeplay.domain.Track;
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
            String email, String profileUrl, String token, String refreshToken) {

        return MemberResponseDTO.LoginResultDTO.builder()
                .email(email)
                .profileUrl(profileUrl)
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
                .harmonyId(harmony.getId())
                .musicId(harmony.getMusic().getId())
                .musicTitle(harmony.getTitle())
                .createdAt(harmony.getCreatedAt())
                .scale(harmony.getScale())
                .genre(harmony.getGenre())
                .bpm(harmony.getBpm())
                .voiceColor(harmony.getVoiceColor())
                .isLiked(
                        likeService.isLikedByUser(
                                member, harmony.getMusic())) // LikeService에서 좋아요 여부 확인
                .build();
    }

    public MemberResponseDTO.GetMyTrackDTO toGetMyTrackDTO(Track track, Member member) {
        return MemberResponseDTO.GetMyTrackDTO.builder()
                .trackId(track.getId())
                .musicId(track.getMusic().getId())
                .musicTitle(track.getTitle())
                .createdAt(track.getCreatedAt())
                .vocalUrl(track.getVocalUrl())
                .instrumentalUrl(track.getInstrumentalUrl())
                .bassUrl(track.getBassUrl())
                .drumsUrl(track.getDrumsUrl())
                .isLiked(
                        likeService.isLikedByUser(
                                member, track.getMusic())) // LikeService에서 좋아요 여부 확인
                .build();
    }

    public static MemberResponseDTO.TaskProgressDTO toTaskProgressDTO(Task task) {
        return MemberResponseDTO.TaskProgressDTO.builder()
                .taskId(task.getId())
                .processStatus(task.getStatus().toString())
                .jobType(task.getJobType().toString())
                .jobId(task.getJobId())
                .build();
    }
}
