package umc.codeplay.service;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.Track;
import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.repository.HarmonyRepository;
import umc.codeplay.repository.MemberRepository;
import umc.codeplay.repository.TrackRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final HarmonyRepository harmonyRepository;
    private final MemberConverter memberConverter;
    private final TrackRepository trackRepository;

    private static final String CHAR_SET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String getMemberProfileImage(String email) {
        return memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND))
                .getProfileUrl();
    }

    public Member joinMember(MemberRequestDTO.JoinDto request) {

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GeneralHandler(ErrorStatus.MEMBER_ALREADY_EXISTS);
        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
        return memberRepository.save(newMember);
    }

    public Member findOrCreateOAuthMember(String email, SocialStatus socialStatus) {

        Member member = memberRepository.findByEmail(email).orElse(null);

        if (member == null) {
            member =
                    Member.builder()
                            .email(email)
                            .role(Role.USER)
                            .password(passwordEncoder.encode(generateRandomPassword(10)))
                            .socialStatus(socialStatus)
                            .build();
            return memberRepository.save(member);
        } else if (member.getSocialStatus() != socialStatus) {
            throw new GeneralHandler(ErrorStatus.AUTHORIZATION_METHOD_ERROR);
        } else {
            return member;
        }
    }

    public SocialStatus getSocialStatus(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return SocialStatus.NONE;
        } else {
            return member.getSocialStatus();
        }
    }

    @Transactional
    public Member updateMember(String email, MemberRequestDTO.UpdateMemberDTO requestDto) {

        // MemberRepository의 findByEmail()을 사용하여 회원 조회
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));
        System.out.println("console log checking");
        // 사용자 입력 값
        String newPassword = requestDto.getNewPassword();
        String currentPassword = requestDto.getCurrentPassword();
        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidParameterException("기존 비밀번호가 일치하지 않습니다.");
            // 기존 비밀번호가 일치하고 새로운 비밀번호 입력값이 있을때 비밀번호 변경
        } else if (newPassword != null && !newPassword.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            member.setPassword(encodedPassword);
        } else {
            throw new InvalidParameterException("새로운 비밀번호를 입력해주세요.");
        }

        memberRepository.save(member);
        return member;
    }

    public List<MemberResponseDTO.GetMyHarmonyDTO> getMyHarmony(Member member) {

        List<Harmony> harmonies = harmonyRepository.findByMusicMember(member);

        return harmonies.stream()
                .map(harmony -> memberConverter.toGetMyHarmonyDTO(harmony, member))
                .collect(Collectors.toList());
    }

    public List<MemberResponseDTO.GetMyTrackDTO> getMyTrack(Member member) {

        List<Track> tracks = trackRepository.findByMusicMember(member);

        return tracks.stream()
                .map(track -> memberConverter.toGetMyTrackDTO(track, member))
                .collect(Collectors.toList());
    }

    public MemberResponseDTO.GetAllByMusicTitleDTO getAllByMusicTitle(Member member, Music music) {

        List<Harmony> harmonies = harmonyRepository.findByMusicAndMember(member, music);
        List<Track> tracks = trackRepository.findByMusicAndMember(member, music);

        List<MemberResponseDTO.GetMyHarmonyDTO> harmonyDTOs =
                harmonies.stream()
                        .map(harmony -> memberConverter.toGetMyHarmonyDTO(harmony, member))
                        .collect(Collectors.toList());

        List<MemberResponseDTO.GetMyTrackDTO> trackDTOs =
                tracks.stream()
                        .map(track -> memberConverter.toGetMyTrackDTO(track, member))
                        .collect(Collectors.toList());

        // DTO를 하나의 객체로 묶어서 반환
        return new MemberResponseDTO.GetAllByMusicTitleDTO(harmonyDTOs, trackDTOs);
    }

    // 음원 제목으로 my harmony 검색
    public List<MemberResponseDTO.GetMyHarmonyDTO> getHarmonyByMusicTitle(
            Member member, Music music) {
        List<Harmony> harmonies = harmonyRepository.findByMusicAndMember(member, music);

        return harmonies.stream()
                .map(harmony -> memberConverter.toGetMyHarmonyDTO(harmony, member))
                .collect(Collectors.toList());
    }

    // 음원 제목으로 my track 검색
    public List<MemberResponseDTO.GetMyTrackDTO> getTrackByMusicTitle(Member member, Music music) {
        List<Track> tracks = trackRepository.findByMusicAndMember(member, music);

        return tracks.stream()
                .map(track -> memberConverter.toGetMyTrackDTO(track, member))
                .collect(Collectors.toList());
    }

    // 비밀번호 찾기 이후 변경
    @Transactional
    public boolean newPassword(String email, String newPassword) {

        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        member.encodePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        return true;
    }

    public static String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(index));
        }
        return sb.toString();
    }
}
