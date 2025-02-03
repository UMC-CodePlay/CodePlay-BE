package umc.codeplay.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Member;
import umc.codeplay.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return org.springframework.security.core.userdetails.User.withUsername(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }
}
