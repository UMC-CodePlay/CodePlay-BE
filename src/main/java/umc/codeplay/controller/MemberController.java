package umc.codeplay.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.config.security.CustomUserDetails;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.repository.MemberRepository;
import umc.codeplay.repository.MusicRepository;
import umc.codeplay.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberConverter memberConverter;
    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;

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

    @Operation(summary = "마이페이지 화성분석 탭 결과", description = "로그인한 상태에서 마이페이지")
    @GetMapping("/mypage/harmony")
    public ApiResponse<List<MemberResponseDTO.GetMyHarmonyDTO>> getMyHarmony(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        List<MemberResponseDTO.GetMyHarmonyDTO> results = memberService.getMyHarmony(member);

        return ApiResponse.onSuccess(results);
    }

    @Operation(summary = "마이페이지 세션분리 탭 결과", description = "로그인한 상태에서 마이페이지")
    @GetMapping("/mypage/track")
    public ApiResponse<List<MemberResponseDTO.GetMyTrackDTO>> getMyTracks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        List<MemberResponseDTO.GetMyTrackDTO> results = memberService.getMyTrack(member);

        return ApiResponse.onSuccess(results);
    }

    @Operation(summary = "마이페이지 전체 검색", description = "파라미터 musicTitle에 음원 제목으로 검색")
    @GetMapping("/mypage/search")
    public ApiResponse<MemberResponseDTO.GetAllByMusicTitleDTO> getByMusicTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String musicTitle) {

        // 현재 로그인한 사용자 검색
        String email = userDetails.getUsername();
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Music> musics = musicRepository.findAllByTitleContaining(musicTitle);

        List<MemberResponseDTO.GetMyHarmonyDTO> harmonyDTOs = new ArrayList<>();
        List<MemberResponseDTO.GetMyTrackDTO> trackDTOs = new ArrayList<>();

        for (Music music : musics) {
            harmonyDTOs.addAll(memberService.getHarmonyByMusicTitle(member, music));
            trackDTOs.addAll(memberService.getTrackByMusicTitle(member, music));
        }

        MemberResponseDTO.GetAllByMusicTitleDTO results =
                new MemberResponseDTO.GetAllByMusicTitleDTO(harmonyDTOs, trackDTOs);

        return ApiResponse.onSuccess(results);
    }

    @Operation(summary = "마이페이지 화성분석 검색", description = "파라미터 musicTitle에 음원 제목으로 검색")
    @GetMapping("/mypage/harmony/search")
    public ApiResponse<List<MemberResponseDTO.GetMyHarmonyDTO>> getHarmonyByMusicTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String musicTitle) {
        String email = userDetails.getUsername();
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Music> musics = musicRepository.findAllByTitleContaining(musicTitle);
        List<MemberResponseDTO.GetMyHarmonyDTO> results =
                musics.stream()
                        .flatMap(
                                music ->
                                        memberService
                                                .getHarmonyByMusicTitle(member, music)
                                                .stream())
                        .collect(Collectors.toList());

        return ApiResponse.onSuccess(results);
    }

    @Operation(summary = "마이페이지 세션분리 검색", description = "파라미터 musicTitle에 음원 제목으로 검색")
    @GetMapping("/mypage/track/search")
    public ApiResponse<List<MemberResponseDTO.GetMyTrackDTO>> getTrackByMusicTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String musicTitle) {
        String email = userDetails.getUsername();
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Music> musics = musicRepository.findAllByTitleContaining(musicTitle);
        List<MemberResponseDTO.GetMyTrackDTO> results =
                musics.stream()
                        .flatMap(
                                music -> memberService.getTrackByMusicTitle(member, music).stream())
                        .collect(Collectors.toList());

        return ApiResponse.onSuccess(results);
    }
}
