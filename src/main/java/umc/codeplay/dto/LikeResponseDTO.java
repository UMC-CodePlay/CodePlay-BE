package umc.codeplay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import umc.codeplay.domain.mapping.MusicLike;

public class LikeResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class addLikeResponseDTO {
        Long musicId;
        MusicLike like;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class removeLikeResponseDTO {
        Long musicId;
    }
}
