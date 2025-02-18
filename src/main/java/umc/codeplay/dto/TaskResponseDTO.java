package umc.codeplay.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TaskResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HarmonyDTO {
        // 화성분석Id, 음원Id, 음원제목, 생성 날짜, 키, bpm, 평균음압
        Long harmonyId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String scale;
        String genre;
        Integer bpm;
        String voiceColor;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackDTO {
        // 세션 분리 harmonyId, 음원 harmonyId, 음원 제목, 생성일자, 보컬 파일 url, 반주 url, 베이스 파일 url, 드럼 파일 url,
        Long trackId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        String vocalUrl;
        String instrumentalUrl;
        String bassUrl;
        String drumsUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemixDTO {
        Long remixId;
        Long musicId;
        String musicTitle;
        LocalDateTime createdAt;
        Integer scaleModulation;
        Double tempoRatio;
        Double reverbAmount;
        Boolean isCorusOn;
        String resultMusicUrl;
        private Long parentRemixId; // 부모 Remix의 ID
        private List<RemixDTO> childRemixList; // 자식 Remix 리스트
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetByTaskIdDTO {

        private List<HarmonyDTO> harmonies;
        private List<TrackDTO> tracks;
        private List<RemixDTO> remixes;
    }
}
