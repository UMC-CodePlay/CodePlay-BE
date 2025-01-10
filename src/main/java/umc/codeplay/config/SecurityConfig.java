package umc.codeplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import umc.codeplay.jwt.JwtAuthenticationFilter;
import umc.codeplay.jwt.JwtUtil;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // AuthenticationManager 를 빈으로 등록 (스프링 시큐리티 6.x 이상)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 세션을 사용하지 않도록 설정
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable) // JWT 사용 시 일반적으로 CSRF 는 disable
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // 로그인, 회원가입 등 토큰 없이 접근해야 하는 API 허용
                                        .requestMatchers("/auth/login")
                                        .permitAll()
                                        // 그 외 나머지는 인증 필요
                                        .anyRequest()
                                        .authenticated())
                // 폼 로그인 등 기본 기능 비활성화 (JWT 만 쓰려면)
                .formLogin(Customizer.withDefaults())
                // .formLogin(form -> form.disable()) // 더 엄격하게 폼 로그인 완전히 비활성화할 수도 있음
                .logout(AbstractHttpConfigurer::disable);

        // 커스텀 JWT 필터 추가
        // UsernamePasswordAuthenticationFilter 이전에 동작하도록 설정
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil),
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
                        .class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
