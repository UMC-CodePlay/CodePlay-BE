package umc.codeplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;

public interface HarmonyRepository extends JpaRepository<Harmony, Long> {

    // 특정 사용자의 Harmony 리스트 조회
    List<Harmony> findByMusicMember(Member member);

    // 특정 사용자의 Harmony 리스트 중 music의 harmony 조회
    @Query("SELECT h FROM Harmony h " + "WHERE h.music = :music AND h.music.member = :member")
    List<Harmony> findByMusicAndMember(@Param("member") Member member, @Param("music") Music music);
}
