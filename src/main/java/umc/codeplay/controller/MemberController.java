package umc.codeplay.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.config.security.CustomUserDetails;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberConverter memberConverter;

    @PutMapping("/update")
    public ApiResponse<MemberResponseDTO.UpdateResultDTO> updateMember(
            @AuthenticationPrincipal
                    CustomUserDetails
                            userDetails, // 현재 로그인된 사용자 정보, email로 조회하기 위해 customUserDetails 사용
            @RequestBody MemberRequestDTO.UpdateMemberDTO requestDto) {

        Member updatedMember = memberService.updateMember(userDetails.getUsername(), requestDto);
        MemberResponseDTO.UpdateResultDTO responseDto =
                memberConverter.toUpdateResultDTO(updatedMember);

        return ApiResponse.onSuccess(responseDto);
    }
}
