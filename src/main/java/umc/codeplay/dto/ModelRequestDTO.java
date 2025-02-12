package umc.codeplay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ModelRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HarmonyRequestDTO {

        @NotNull(message = "taskId 는 필수 입력 값입니다.") private Long taskId;

        @NotBlank(message = "scale 는 필수 입력 값입니다.")
        private String scale;

        @NotNull(message = "bpm 는 필수 입력 값입니다.") private Integer bpm;

        @NotBlank(message = "genre 는 필수 입력 값입니다.")
        private String genre;

        @NotBlank(message = "voiceColor 는 필수 입력 값입니다.")
        private String voiceColor;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackRequestDTO {

        @NotNull(message = "taskId 는 필수 입력 값입니다.") private Long taskId;

        @NotNull(message = "isTwoStem 는 필수 입력 값입니다.") private Boolean isTwoStem;

        private String vocalUrl;
        private String instrumentalUrl;
        private String bassUrl;
        private String drumsUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemixRequestDTO {

        @NotNull(message = "taskId 는 필수 입력 값입니다.") private Long taskId;

        @NotBlank(message = "resultMusicUrl 는 필수 입력 값입니다.")
        private String resultMusicUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskFailDTO {

        @NotNull(message = "taskId 는 필수 입력 값입니다.") private Long taskId;

        @NotBlank(message = "failMessage 는 필수 입력 값입니다.")
        private String failMessage;
    }
}
