package umc.codeplay.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.converter.MusicLikeConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.mapping.MusicLike;
import umc.codeplay.dto.LikeRequestDTO;
import umc.codeplay.repository.MemberRepository;
import umc.codeplay.repository.MusicLikeRepository;
import umc.codeplay.repository.MusicRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final MusicRepository musicRepository;
    private final MemberRepository memberRepository;
    private final MusicLikeRepository musicLikeRepository;

    public MusicLike addLike(String username, LikeRequestDTO.addLikeRequestDTO request) {

        Music music =
                musicRepository
                        .findById(request.getMusicId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MUSIC_NOT_FOUND));

        Member member =
                memberRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        MusicLike newLike = MusicLikeConverter.toMusicLike(member, music);
        return musicLikeRepository.save(newLike);
    }

    public Music removeLike(String username, LikeRequestDTO.removeLikeRequestDTO request) {
        Music music =
                musicRepository
                        .findById(request.getMusicId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MUSIC_NOT_FOUND));

        Member member =
                memberRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        MusicLike musicLike =
                musicLikeRepository
                        .findByMemberAndMusic(member, music)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.LIKE_NOT_FOUND));

        musicLikeRepository.delete(musicLike);
        return music;
    }
}
