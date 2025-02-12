package umc.codeplay.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

public class MemberRequestDTO {

    @Getter
    public static class JoinDto {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password;
    }

    @Getter
    public static class LoginDto {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password;
    }

    @Getter
    public static class ResetPasswordDTO {
        @Email(message = "이메일 형식이 아닙니다.")
        String email;
    }

    @Getter
    public static class CheckVerificationCodeDTO {
        String email;
        String code;
    }

    @Getter
    public static class ChangePasswordDTO {
        String email;
        String newPassword;
    }

    @Getter
    @Setter
    public static class UpdateMemberDTO {

        @NotBlank(message = "기존 비밀번호는 필수 입력값입니다.")
        String currentPassword;

        @NotBlank(message = "새로운 비밀번호는 필수 입력값입니다.")
        String newPassword;
    }

    @Getter
    public static class SearchByMusicTitleDTO {

        @NotBlank(message = "음원 제목은 필수 입력값입니다.")
        String musicTitle;
    }

    @Getter
    public static class StemTaskDTO {

        @NotNull(message = "음원 id 는 필수 입력값입니다.") Long musicId;

        @NotBlank(message = "빈칸이라도 아무거나 보내주세요.")
        String twoStemConfig;
    }

    @Getter
    public static class HarmonyTaskDTO {

        @NotNull(message = "음원 id 는 필수 입력값입니다.") private Long musicId;
    }

    @Getter
    public static class RemixTaskDTO {

        @NotNull(message = "음원 id 는 필수 입력값입니다.") private Long musicId;

        private Long parentRemixId;

        @Min(value = -12, message = "음원 id 는 -12 이상의 값이어야 합니다.")
        @Max(value = 12, message = "음원 id 는 12 이하의 값이어야 합니다.")
        private Integer scaleModulation;

        @DecimalMin(value = "0.1", message = "음원 id 는 0.1 이상의 값이어야 합니다.")
        @DecimalMax(value = "4.0", message = "음원 id 는 4.0 이하의 값이어야 합니다.")
        private Double tempoRatio;

        @DecimalMin(value = "0.0", message = "음원 id 는 0.0 이상의 값이어야 합니다.")
        @DecimalMax(value = "1.0", message = "음원 id 는 1.0 이하의 값이어야 합니다.")
        private Double reverbAmount;

        private Boolean isChorusOn;
    }

    @Getter
    public static class getTaskDTO {

        @NotNull(message = "task id 는 필수 입력값입니다.") Long taskId;
    }
}
