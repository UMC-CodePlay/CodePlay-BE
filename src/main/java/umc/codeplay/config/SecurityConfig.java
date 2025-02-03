package umc.codeplay.config;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.jwt.JwtAuthenticationFilter;
import umc.codeplay.jwt.JwtUtil;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    // AuthenticationManager 를 빈으로 등록 (스프링 시큐리티 6.x 이상)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
        http
                // 세션을 사용하지 않도록 설정
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable) // JWT 사용 시 일반적으로 CSRF 는 disable
                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // 로그인, 회원가입 등 토큰 없이 접근해야 하는 API 허용
                                        .requestMatchers(
                                                "/oauth/**",
                                                "/health",
                                                "/health/s3",
                                                "/auth/**",
                                                "/member/**",
                                                "/v2/api-docs",
                                                "/v3/api-docs",
                                                "/v3/api-docs/**",
                                                "/swagger-resources",
                                                "/swagger-resources/**",
                                                "/configuration/ui",
                                                "/configuration/security",
                                                "/swagger-ui/**",
                                                "/webjars/**",
                                                "/swagger-ui.html")
                                        .permitAll()
                                        // 그 외 나머지는 인증 필요
                                        .anyRequest()
                                        .authenticated())
                // 폼 로그인 등 기본 기능 비활성화 (JWT 만 쓰려면)
                .formLogin(Customizer.withDefaults())
                // .formLogin(form -> form.disable()) // 더 엄격하게
                // 폼 로그인 완전히 비활성화할 수도 있음
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

    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private final ObjectMapper objectMapper;

        public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException)
                throws IOException, ServletException {

            ApiResponse<Void> apiResponse =
                    ApiResponse.onFailure(
                            ErrorStatus.NOT_AUTHORIZED.getCode(),
                            ErrorStatus.NOT_AUTHORIZED.getMessage(),
                            null);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
        }
    }
}
