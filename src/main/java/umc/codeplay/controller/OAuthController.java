package umc.codeplay.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.config.GoogleOAuthProperties;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.jwt.JwtUtil;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final GoogleOAuthProperties googleOAuthProperties;
    private final MemberService memberService;

    @GetMapping("/authorize/google")
    public RedirectView redirectToGoogleAuth() {
        // CSRF 방어용 state, PKCE(code_challenge)..는 굳이

        String url =
                googleOAuthProperties.getAuthorizationUri()
                        + "?client_id="
                        + googleOAuthProperties.getClientId()
                        + "&redirect_uri="
                        + googleOAuthProperties.getRedirectUri() // 설정된 리다이렉트로 변경
                        + "&response_type=code"
                        + "&scope="
                        + googleOAuthProperties.getScope()
                        + "&access_type=offline" // refresh_token 받고 싶다면
                        + "&prompt=consent"; // 매번 동의화면을 띄우려면

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    @GetMapping("/callback/google")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> googleCallback(
            @RequestParam("code") String code) {

        // (1) 받은 code로 구글 토큰 엔드포인트에 Access/ID Token 교환
        Map<String, Object> tokenResponse = requestGoogleToken(code);

        // (2) 받아온 Access Token(or ID Token)을 통해 사용자 정보 가져오기
        String idToken = (String) tokenResponse.get("id_token"); // OIDC
        String accessToken = (String) tokenResponse.get("access_token");

        // (3) 구글 UserInfo Endpoint (또는 idToken 파싱)으로 이메일, 프로필 등 조회
        Map<String, Object> userInfo = requestGoogleUserInfo(accessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // (4) 우리 DB에서 회원 조회 or 생성
        Member member = memberService.findOrCreateOAuthMember(email, name, SocialStatus.GOOGLE);

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

    private Map<String, Object> requestGoogleToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", googleOAuthProperties.getRedirectUri()); // ?
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(googleOAuthProperties.getTokenUri(), request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new GeneralHandler(ErrorStatus.GOOGLE_TOKEN_REQUEST_FAILED);
    }

    private Map<String, Object> requestGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        googleOAuthProperties.getUserInfoUri(), HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new GeneralHandler(ErrorStatus.GOOGLE_USERINFO_REQUEST_FAILED);
    }
}
