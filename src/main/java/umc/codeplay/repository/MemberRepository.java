package umc.codeplay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
