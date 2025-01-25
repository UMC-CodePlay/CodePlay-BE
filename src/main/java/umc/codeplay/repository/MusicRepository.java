package umc.codeplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Music;

public interface MusicRepository extends JpaRepository<Music, Long> {}
