package umc.codeplay.jwt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import umc.codeplay.config.security.CustomUserDetails;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {

        // 1. Authorization 헤더 파싱
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. 토큰 유효성 검사
            if (jwtUtil.validateToken(token)
                    && (jwtUtil.getTypeFromToken(token).equals("access"))) {
                // 3. 토큰에서 사용자 정보 추출
                String email = jwtUtil.getEmailFromToken(token);
                System.out.println(email);
                List<String> roles = jwtUtil.getRolesFromToken(token);

                List<GrantedAuthority> authorities =
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                // CustomUserDetails 객체 생성 후 저장
                CustomUserDetails userDetails = new CustomUserDetails(email, "", authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                System.out.println(authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
