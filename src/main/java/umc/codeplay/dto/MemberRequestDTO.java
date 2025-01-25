package umc.codeplay.dto;

import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class JoinDto {
        String name;
        String email;
        String password;
    }

    @Getter
    public static class LoginDto {
        String email;
        String password;
    }
}
