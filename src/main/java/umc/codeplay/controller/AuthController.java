package umc.codeplay.controller;

import java.util.Collection;
import java.util.stream.Collectors;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.jwt.JwtUtil;
import umc.codeplay.service.EmailService;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(
            @Validated @RequestBody MemberRequestDTO.LoginDto request) {
        if (memberService.getSocialStatus(request.getEmail()) != SocialStatus.NONE) {
            throw new GeneralHandler(ErrorStatus.AUTHORIZATION_METHOD_ERROR);
        }

        // 아이디/비밀번호를 사용해 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // 실제 인증 수행
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Role 정보 가져오기
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // 인증에 성공했다면, JWT 토큰 생성 후 반환
            String token = jwtUtil.generateToken(authentication.getName(), authorities);
            String refreshToken =
                    jwtUtil.generateRefreshToken(authentication.getName(), authorities);
            return ApiResponse.onSuccess(
                    MemberConverter.toLoginResultDTO(
                            request.getEmail(), token, refreshToken)); // 예시로 토큰만 문자열로 반환
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.ID_OR_PASSWORD_WRONG);
        }
    }

    @PostMapping("/signup")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(
            @Validated @RequestBody MemberRequestDTO.JoinDto request) {
        Member member = memberService.joinMember(request);
        MemberResponseDTO.JoinResultDTO newJoinResult = MemberConverter.toJoinResultDTO(member);

        return ApiResponse.onSuccess(newJoinResult);
    }

    @PostMapping("/refresh")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> refresh(
            @RequestHeader("Refresh-Token") @NotNull(message = "리프레시 토큰은 필수 헤더입니다.") String refreshToken,
            @Validated @RequestParam("email") @NotBlank(message = "이메일은 필수 입력값입니다.") String email) {
        // 리프레시 토큰 유효성 검사
        if (jwtUtil.validateToken(refreshToken)
                && (jwtUtil.getTypeFromToken(refreshToken).equals("refresh"))) {
            // 리프레시 토큰에서 사용자명 추출
            String usernameFromToken = jwtUtil.getEmailFromToken(refreshToken);

            if (!email.equals(usernameFromToken)) {
                throw new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
            }

            // 사용자 권한 정보 가져오기
            Collection<? extends GrantedAuthority> authorities =
                    jwtUtil.getRolesFromToken(refreshToken).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtUtil.generateToken(usernameFromToken, authorities);

            return ApiResponse.onSuccess(
                    MemberConverter.toLoginResultDTO(usernameFromToken, newAccessToken, null));
        } else {
            throw new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
    }

    // 비밀번호 찾기 및 변경. 이메일 인증
    @PostMapping("/password/reset/request")
    public ApiResponse<String> resetPasswordRequest(
            @RequestBody MemberRequestDTO.ResetPasswordDTO request) throws MessagingException {
        emailService.sendCode(request.getEmail());
        return ApiResponse.onSuccess("메일로 인증번호가 전송되었습니다.");
    }

    // 비밀번호 찾기 및 변경. 인증 코드 확인
    @PostMapping("/password/reset/verify")
    public ApiResponse<String> resetPasswordVerify(
            @RequestBody MemberRequestDTO.CheckVerificationCodeDTO request) {
        boolean isValid = emailService.verifyCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ApiResponse.onSuccess("인증에 성공하였습니다.");
            // 이후에 비밀번호 변경 페이지 연결해 주어야 함.
        } else {
            throw new GeneralHandler(ErrorStatus.EMAIL_CODE_ERROR);
        }
    }
}
