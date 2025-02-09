package umc.codeplay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ModelRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HarmonyRequestDTO {
        private Long taskId;
        private String scale;
        private Integer bpm;
        private String genre;
        private String voiceColor;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackRequestDTO {
        private Long taskId;
        private String vocalUrl;
        private String instrumentalUrl;
        private String bassUrl;
        private String drumsUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemixRequestDTO {
        private Long taskId;
        private String resultMusicUrl;
    }
}
