package umc.codeplay.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.service.MusicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

    private final MusicService musicService;

    @DeleteMapping("/{musicId}")
    public ApiResponse<Long> delete(@PathVariable Long musicId) {

        musicService.deleteMusic(musicId);
        return ApiResponse.onSuccess(musicId);
    }
}
