package umc.codeplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Member;

public interface HarmonyRepository extends JpaRepository<Harmony, Long> {

    // 특정 사용자의 Harmony 리스트 조회
    List<Harmony> findByMusicMember(Member member);
}
