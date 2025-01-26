package umc.codeplay.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.converter.MusicLikeConverter;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.mapping.MusicLike;
import umc.codeplay.dto.LikeRequestDTO;
import umc.codeplay.dto.LikeResponseDTO;
import umc.codeplay.service.LikeService;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like/add")
    public ApiResponse<LikeResponseDTO.addLikeResponseDTO> addLike(
            @RequestBody @Valid LikeRequestDTO.addLikeRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        MusicLike like = likeService.addLike(username, request);
        // 로그인 한 username, request 받아서 MusicLike 추가하고 해당 musicId 반환

        return ApiResponse.onSuccess(MusicLikeConverter.toLikeResponseDTO(like));
    }

    @PostMapping("/like/remove")
    public ApiResponse<LikeResponseDTO.removeLikeResponseDTO> removeLike(
            @RequestBody @Valid LikeRequestDTO.removeLikeRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Music music = likeService.removeLike(username, request);

        return ApiResponse.onSuccess(MusicLikeConverter.toRemoveLikeResponseDTO(music));
    }
}
