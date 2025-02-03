package umc.codeplay.dto;

import java.time.LocalDateTime;

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
        // 음원Id,음원제목, 생성 날짜, 키, bpm, 평균음압, 즐겨찾기 여부
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String harmonyKey;
        Integer bpm;
        Integer soundPressure;
        Boolean isLiked;
    }

    //    @Builder
    //    @Getter
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    public static class GetMyTrackDTO {
    //        // 음원제목, 생성일자, 즐겨찾기 여부
    //
    //    }
}
