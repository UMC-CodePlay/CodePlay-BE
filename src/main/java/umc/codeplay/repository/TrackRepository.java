package umc.codeplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.Track;

public interface TrackRepository extends JpaRepository<Track, Long> {
    // 특정 사용자의 Track 리스트 조회
    List<Track> findByMusicMember(Member member);

    // 특정 사용자의 Harmony 리스트 중 music의 harmony 조회
    @Query("SELECT t FROM Track t " + "WHERE t.music = :music AND t.music.member = :member")
    List<Track> findByMusicAndMember(@Param("member") Member member, @Param("music") Music music);
}
