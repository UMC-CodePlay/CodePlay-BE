package umc.codeplay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.mapping.MusicLike;

public interface MusicLikeRepository extends JpaRepository<MusicLike, Long> {
    Optional<MusicLike> findByMemberAndMusic(Member member, Music music);

    boolean existsByMemberAndMusic(Member member, Music music);
}
