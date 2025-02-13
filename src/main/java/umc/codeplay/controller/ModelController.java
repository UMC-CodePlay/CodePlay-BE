package umc.codeplay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.domain.enums.ProcessStatus;
import umc.codeplay.dto.ModelRequestDTO;
import umc.codeplay.dto.ModelResponseDTO;
import umc.codeplay.service.ModelService;
import umc.codeplay.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/python-model")
@Tag(name = "py-model-controller", description = "Python 로컬 서버의 분석 결과를 저장하는 API (프론트엔드 사용 X)")
public class ModelController {

    private final ModelService modelService;
    private final TaskService taskService;

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

    @Operation(
            summary = "작업 실패 업데이트",
            description = "Python 서버의 작업 실패 결과를 저장합니다. 프론트엔드에서 사용하지 않습니다.")
    @PostMapping("/fail")
    public ApiResponse<ModelResponseDTO.TaskFailDTO> updateTaskFail(
            @RequestBody ModelRequestDTO.TaskFailDTO request) {
        // TODO: 작업 등록 시 생성되었던 엔티티 제거 로직 추가.
        return ApiResponse.onSuccess(
                ModelResponseDTO.TaskFailDTO.builder()
                        .taskId(
                                taskService
                                        .updateTaskStatus(request.getTaskId(), ProcessStatus.FAILED)
                                        .getId())
                        .build());
    }
}
