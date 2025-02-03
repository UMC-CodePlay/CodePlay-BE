package umc.codeplay.dto;

import lombok.Getter;

public class LikeRequestDTO {

    @Getter
    public static class addLikeRequestDTO {
        Long musicId;
    }

    @Getter
    public static class removeLikeRequestDTO {
        Long musicId;
    }
}
