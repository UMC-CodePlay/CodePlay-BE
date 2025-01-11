package umc.codeplay.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 숨겨야합니다
    private final String SECRET_KEY = "thisisthesecretkeyverylongsecretkey";

    // 30분 만료
    private final long EXPIRATION_TIME = 1000 * 60 * 30;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // JWT 토큰 생성
    public String generateToken(
            String username, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();

        List<String> roleNames =
                authorities.stream()
                        .map(GrantedAuthority::getAuthority) // "ROLE_ADMIN" 등
                        .toList();

        return Jwts.builder()
                .setSubject(username) // 사용자 식별 정보
                .setIssuedAt(now)
                .claim("roles", roleNames) // 발급 시간
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME)) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명 (HS256 알고리즘)
                .compact();
    }

    // JWT 토큰에서 username 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료되었거나 서명 검증 실패 등
            return false;
        }
    }

    // 토큰 클레임 요청
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 서명 검증을 위한 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        // "roles"라는 이름의 클레임에서 리스트를 꺼냄
        // 저장할 때 List<String>으로 넣었다면, get("roles", List.class) 로 받는 것이 간단함
        List<String> roles = claims.get("roles", List.class);

        // roles 가 null 일 수도 있으므로 안전 처리
        if (roles == null) {
            return List.of(); // 빈 리스트 반환
        }
        return roles;
    }
}
