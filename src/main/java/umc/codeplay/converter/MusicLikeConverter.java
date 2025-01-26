package umc.codeplay.converter;

import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.mapping.MusicLike;
import umc.codeplay.dto.LikeResponseDTO;

public class MusicLikeConverter {

    public static MusicLike toMusicLike(Member member, Music music) {

        return MusicLike.builder().member(member).music(music).build();
    }

    public static LikeResponseDTO.addLikeResponseDTO toLikeResponseDTO(MusicLike like) {

        return LikeResponseDTO.addLikeResponseDTO
                .builder()
                .musicId(like.getMusic().getId())
                .like(like)
                .build();
    }

    public static LikeResponseDTO.removeLikeResponseDTO toRemoveLikeResponseDTO(Music music) {
        return LikeResponseDTO.removeLikeResponseDTO.builder().musicId(music.getId()).build();
    }
}
