package umc.codeplay.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.config.properties.BaseOAuthProperties;
import umc.codeplay.config.properties.GoogleOAuthProperties;
import umc.codeplay.config.properties.KakaoOAuthProperties;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.jwt.JwtUtil;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Validated
@Tag(name = "oauth-controller", description = "외부 소셜 로그인 서비스 연동 API, JWT 토큰 헤더 포함을 필요로 하지 않습니다.")
public class OAuthController {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final GoogleOAuthProperties googleOAuthProperties;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final MemberService memberService;

    @GetMapping("/authorize/{provider}")
    @Operation(
            summary = "소셜 로그인 서비스로 로그인합니다.",
            description =
                    "{provider}엔 google, kakao 가 들어갈 수 있습니다. 해당 소셜 로그인 서비스로 리다이렉트합니다. 로그인이 완료되면 스프링 서버에서 사용 가능한 JWT 토큰/리프레시 토큰을 반환합니다.")
    public RedirectView redirectToOAuth(@PathVariable("provider") String provider) {
        // CSRF 방어용 state, PKCE(code_challenge)..는 굳이
        BaseOAuthProperties properties =
                switch (provider) {
                    case "google" -> googleOAuthProperties;
                    case "kakao" -> kakaoOAuthProperties;
                    default -> throw new GeneralHandler(ErrorStatus.INVALID_OAUTH_PROVIDER);
                };

        String url = properties.getUrl();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    @Hidden
    @GetMapping("/callback/{provider}")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> OAuthCallback(
            @RequestParam("code") String code, @PathVariable("provider") String provider) {
        BaseOAuthProperties properties =
                switch (provider) {
                    case "google" -> googleOAuthProperties;
                    case "kakao" -> kakaoOAuthProperties;
                    default -> throw new GeneralHandler(ErrorStatus.INVALID_OAUTH_PROVIDER);
                };
        // (1) 받은 code 로 구글 토큰 엔드포인트에 Access/ID Token 교환
        Map<String, Object> tokenResponse = requestOAuthToken(code, properties);

        // (2) 받아온 Access Token(or ID Token)을 통해 사용자 정보 가져오기
        //        String idToken = (String) tokenResponse.get("id_token"); // OIDC
        String accessToken = (String) tokenResponse.get("access_token");
        Map<String, Object> userInfo = requestOAuthUserInfo(accessToken, properties);
        String email = null;
        //        String name = null;
        switch (provider) {
            case "google" -> {
                // (3-a) 구글 UserInfo Endpoint 로 이메일, 프로필 등 조회
                email = (String) userInfo.get("email");
                //                name = (String) userInfo.get("name");
            }
            case "kakao" -> {
                // (3-b) 카카오 UserInfo Endpoint 로 이메일, 프로필 등 조회
                Map<String, Object> kakaoAccount =
                        (Map<String, Object>) userInfo.get("kakao_account");
                //                Map<String, Object> kakaoProperties =
                //                        (Map<String, Object>) userInfo.get("properties");
                email = (String) kakaoAccount.get("email");
                //                name = (String) kakaoProperties.get("nickname");
            }
        }

        // (4) 우리 DB에서 회원 조회 or 생성
        Member member =
                memberService.findOrCreateOAuthMember(
                        email, SocialStatus.valueOf(provider.toUpperCase()));

        // (5) JWTUtil 이용해서 Access/Refresh 토큰 발급
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));

        String serviceAccessToken = jwtUtil.generateToken(email, authorities);
        String serviceRefreshToken = jwtUtil.generateRefreshToken(email, authorities);

        // (6) 최종적으로 JWT(액세스/리프레시)를 프론트에 응답
        return ApiResponse.onSuccess(
                MemberResponseDTO.LoginResultDTO.builder()
                        .email(email)
                        .token(serviceAccessToken)
                        .refreshToken(serviceRefreshToken)
                        .build());
    }

    private Map<String, Object> requestOAuthToken(String code, BaseOAuthProperties properties) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(properties.getTokenUri(), request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new GeneralHandler(ErrorStatus.OAUTH_TOKEN_REQUEST_FAILED);
    }

    private Map<String, Object> requestOAuthUserInfo(
            String accessToken, BaseOAuthProperties properties) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        properties.getUserInfoUri(), HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new GeneralHandler(ErrorStatus.OAUTH_USERINFO_REQUEST_FAILED);
    }
}
