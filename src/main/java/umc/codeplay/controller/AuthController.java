package umc.codeplay.controller;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.jwt.JwtUtil;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(
            @RequestBody MemberRequestDTO.LoginDto request) {
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
            return ApiResponse.onSuccess(
                    MemberConverter.toLoginResultDTO(request.getEmail(), token)); // 예시로 토큰만 문자열로 반환
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.ID_OR_PASSWORD_WRONG);
        }
    }

    @PostMapping("/signup")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(
            @RequestBody MemberRequestDTO.JoinDto request) {
        Member member = memberService.joinMember(request);
        MemberResponseDTO.JoinResultDTO newJoinResult = MemberConverter.toJoinResultDTO(member);

        return ApiResponse.onSuccess(newJoinResult);
    }
}
