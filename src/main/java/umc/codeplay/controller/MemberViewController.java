package umc.codeplay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberViewController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String join(@RequestBody MemberRequestDTO.JoinDto request) {
        Member member = memberService.joinMember(request);
        MemberResponseDTO.JoinResultDTO newJoinResultDTO = MemberConverter.toJoinResultDTO(member);
        // ApiResponse 세팅 필요.
        // return ApiResponse.onSuccess(newJoinResultDTO);
        return null;
    }
}
