package umc.codeplay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class JoinDto {

        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name;

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
}
