package umc.codeplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Member;
import umc.codeplay.domain.Track;

public interface TrackRepository extends JpaRepository<Track, Long> {
    // 특정 사용자의 Track 리스트 조회
    List<Track> findByMusicMember(Member member);
}
