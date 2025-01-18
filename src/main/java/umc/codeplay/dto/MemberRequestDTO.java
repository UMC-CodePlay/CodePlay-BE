package umc.codeplay.dto;

import lombok.Getter;

import umc.codeplay.domain.enums.Role;

public class MemberRequestDTO {

    @Getter
    public static class JoinDto {
        String name;
        String email;
        String password;
        Role role;
    }

    @Getter
    public static class LoginDto {
        String email;
        String password;
    }
}
