package umc.codeplay.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EmailCodeDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationCode {
        String code;
        LocalDateTime expires;
    }
}
