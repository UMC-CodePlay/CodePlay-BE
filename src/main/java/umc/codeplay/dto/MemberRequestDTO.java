package umc.codeplay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
    @Setter
    public static class UpdateMemberDTO {

        @NotBlank(message = "기존 비밀번호는 필수 입력값입니다.")
        String currentPassword;

        @NotBlank(message = "새로운 비밀번호는 필수 입력값입니다.")
        String newPassword;
    }
}
