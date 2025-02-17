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
        String profileUrl;
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
        // 화성분석Id, 음원Id, 음원제목, 생성 날짜, 키, bpm, 평균음압, 즐겨찾기 여부
        Long harmonyId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String scale;
        String genre;
        Integer bpm;
        String voiceColor;
        Boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyTrackDTO {
        // 세션 분리 harmonyId, 음원 harmonyId, 음원 제목, 생성일자, 보컬 파일 url, 반주 url, 베이스 파일 url, 드럼 파일 url,
        // 즐겨찾기 여부
        Long trackId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String vocalUrl;
        String instrumentalUrl;
        String bassUrl;
        String drumsUrl;
        Boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllByMusicTitleDTO {

        private List<GetMyHarmonyDTO> harmonies;
        private List<GetMyTrackDTO> tracks;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskProgressDTO {
        Long taskId;
        String processStatus;
        String jobType;
        Long jobId;
    }
}
