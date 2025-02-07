package umc.codeplay.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO {
        Long id;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO {
        String email;
        String token;
        String refreshToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateResultDTO {
        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyHarmonyDTO {
        // 화성분석Id,,음원Id,음원제목, 생성 날짜, 키, bpm, 평균음압, 즐겨찾기 여부
        Long harmonyId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String harmonyKey;
        Integer bpm;
        Integer soundPressure;
        Boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyTrackDTO {
        // 세션 분리 id, 음원 id, 음원 제목, 생성일자, 좋아요여부, 기타 파일 url, 드럼 파일 url, 키보드파일 url
        Long trackId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        Boolean isLiked;
        String guitarUrl;
        String drumUrl;
        String keyboardUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllByMusicTitleDTO {

        private List<GetMyHarmonyDTO> harmonies;
        private List<GetMyTrackDTO> tracks;
    }
}
