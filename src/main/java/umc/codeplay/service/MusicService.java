package umc.codeplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.domain.Music;
import umc.codeplay.repository.MusicRepository;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    @Transactional
    public void deleteMusic(Long id) {

        Music music =
                musicRepository
                        .findById(id)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MUSIC_NOT_FOUND));

        musicRepository.deleteById(id);
    }
}
