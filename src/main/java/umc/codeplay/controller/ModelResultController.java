package umc.codeplay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.dto.ModelRequestDTO;
import umc.codeplay.dto.ModelResponseDTO;
import umc.codeplay.service.ModelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/python-model")
@Tag(name = "py-model-controller", description = "Python 로컬 서버의 분석 결과를 저장하는 API (프론트엔드 사용 X)")
public class ModelResultController {

    private final ModelService modelService;

    @Operation(
            summary = "화성 분석 결과 저장",
            description = "Python 서버의 화성 분석 결과를 저장합니다. 프론트엔드에서 사용하지 않습니다.")
    @PostMapping("/harmony")
    public ApiResponse<ModelResponseDTO.HarmonyResponseDTO> updateHarmony(
            @RequestBody ModelRequestDTO.HarmonyRequestDTO harmonyRequestDTO) {

        return ApiResponse.onSuccess(
                ModelResponseDTO.HarmonyResponseDTO.builder()
                        .harmonyId(modelService.setHarmony(harmonyRequestDTO))
                        .build());
    }

    @Operation(
            summary = "세션 분리 결과 저장",
            description = "Python 서버의 세션 분리 결과를 저장합니다. 프론트엔드에서 사용하지 않습니다.")
    @PostMapping("/tracks")
    public ApiResponse<ModelResponseDTO.TrackResponseDTO> updateTrack(
            @RequestBody ModelRequestDTO.TrackRequestDTO trackRequestDTO) {

        return ApiResponse.onSuccess(
                ModelResponseDTO.TrackResponseDTO.builder()
                        .trackId(modelService.setTrack(trackRequestDTO))
                        .build());
    }

    @Operation(summary = "리믹스 결과 저장", description = "Python 서버의 리믹스 결과를 저장합니다. 프론트엔드에서 사용하지 않습니다.")
    @PostMapping("/remix")
    public ApiResponse<ModelResponseDTO.RemixResponseDTO> updateTrack(
            @RequestBody ModelRequestDTO.RemixRequestDTO remixRequestDTO) {

        return ApiResponse.onSuccess(
                ModelResponseDTO.RemixResponseDTO.builder()
                        .remixId(modelService.setRemix(remixRequestDTO))
                        .build());
    }
}
