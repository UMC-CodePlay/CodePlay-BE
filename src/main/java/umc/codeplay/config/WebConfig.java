package umc.codeplay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String[] ALLOWED_METHOD_NAMES = {
        "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해서 cors 허용
                .allowedOrigins("*") // 모든 출처로 부터 요청 허용 - TODO: 변경해야 함
                .allowedMethods(ALLOWED_METHOD_NAMES)
                .allowedHeaders("Content-Type", "Authorization")
                .maxAge(3600) // 캐싱시간 설정
                .exposedHeaders("Authorization"); // 클라이언트가 접근할 수 있는 헤더
    }
}
