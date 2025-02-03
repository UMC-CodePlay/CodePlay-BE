package umc.codeplay.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.config.security.CustomUserDetails;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.repository.MemberRepository;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberConverter memberConverter;
    private final MemberRepository memberRepository;

    @Operation(summary = "유저 정보 업데이트(비밀번호 변경)", description = "기존 비밀번호, 새로운 비밀번호 입력")
    @PutMapping("/update")
    public ApiResponse<MemberResponseDTO.UpdateResultDTO> updateMember(
            @AuthenticationPrincipal
                    CustomUserDetails
                            userDetails, // 현재 로그인된 사용자 정보, email로 조회하기 위해 customUserDetails 사용
            @RequestBody MemberRequestDTO.UpdateMemberDTO requestDto) {

        // userDetails.getUsername() -> 로그인된 사용자의 email
        Member updatedMember = memberService.updateMember(userDetails.getUsername(), requestDto);
        MemberResponseDTO.UpdateResultDTO responseDto =
                memberConverter.toUpdateResultDTO(updatedMember);

        return ApiResponse.onSuccess(responseDto);
    }

    @Operation(summary = "마이페이지 화성분석 탭 결과", description = "")
    @GetMapping("/mypage/harmony")
    public ApiResponse<List<MemberResponseDTO.GetMyHarmonyDTO>> getMyHarmony(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();
        //        Member member = memberRepository.findByEmail(email);
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        List<MemberResponseDTO.GetMyHarmonyDTO> results = memberService.getMyHarmony(member);

        return ApiResponse.onSuccess(results);
    }

    //    @Operation(summary = "마이페이지 세션분리 탭 결과", description = "")
    //    @GetMapping("/mypage/tracks")
    //    public ApiResponse<List<MemberResponseDTO.GetMyTrackDTO>> getMyTracks() {
    //
    //    }

    //    @Operation(summary = "마이페이지 화성분석 탭에서 세션분리", description = "")
    //    @GetMapping("/mypage/harmonys/track")
    //    public ApiResponse<>
    //
    //
    //    @Operation(summary = "마이페이지 세션분리 탭에서 화성분석", description = "")
    //    @GetMapping("/mypage/tracks/harmony")
    //    public ApiResponse<>

}
