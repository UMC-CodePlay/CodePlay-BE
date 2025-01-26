package umc.codeplay.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao.oauth2")
public class KakaoOAuthProperties extends BaseOAuthProperties {
    // BaseOAuthProperties 의 필드를 그대로 상속받아 사용.
}
