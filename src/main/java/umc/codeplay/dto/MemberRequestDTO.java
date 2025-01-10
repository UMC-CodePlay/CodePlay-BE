package umc.codeplay.dto;

import lombok.Getter;

public class MemberRequestDTO {

  @Getter
  public static class JoinDto {
    String name;
    String email;
    String password;
  }
}
