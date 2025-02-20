package umc.codeplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Music;

public interface MusicRepository extends JpaRepository<Music, Long> {
    // 제목으로 음원 찾기
    Music findByTitle(String title);

    List<Music> findAllByTitleContaining(String title);
}
