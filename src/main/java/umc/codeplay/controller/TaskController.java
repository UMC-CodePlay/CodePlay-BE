package umc.codeplay.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.Task;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.service.ModelService;
import umc.codeplay.service.MusicService;
import umc.codeplay.service.TaskService;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Validated
@Tag(name = "task-controller", description = "화성분석/스템분리/리믹스 작업 요청 API")
public class TaskController {

    private final MusicService musicService;
    private final ModelService modelService;
    private final TaskService taskService;

    @Operation(summary = "화성분석 작업 요청", description = "음악 ID를 받아 화성분석 작업을 요청합니다.")
    @PostMapping("/harmony")
    public ApiResponse<MemberResponseDTO.TaskProgressDTO> requestHarmonyTask(
            @RequestBody @Validated MemberRequestDTO.HarmonyTaskDTO request) {
        Music music = musicService.findById(request.getMusicId());

        Task task = modelService.sendHarmonyMessage(music);

        return ApiResponse.onSuccess(MemberConverter.toTaskProgressDTO(task));
    }

    @Operation(summary = "스템분리 작업 요청", description = "음악 ID와 스템분리 설정을 받아 스템분리 작업을 요청합니다.")
    @PostMapping("/stem")
    public ApiResponse<MemberResponseDTO.TaskProgressDTO> requestStemTask(
            @RequestBody @Validated MemberRequestDTO.StemTaskDTO request) {
        Music music = musicService.findById(request.getMusicId());

        Task task = modelService.sendTrackMessage(music, request.getTwoStemConfig());

        return ApiResponse.onSuccess(MemberConverter.toTaskProgressDTO(task));
    }

    @Operation(summary = "리믹스 작업 요청", description = "음악 ID와 리믹스 설정을 받아 리믹스 작업을 요청합니다.")
    @PostMapping("/remix")
    public ApiResponse<MemberResponseDTO.TaskProgressDTO> requestRemixTask(
            @RequestBody @Validated MemberRequestDTO.RemixTaskDTO request) {
        Music music = musicService.findById(request.getMusicId());

        Task task = modelService.sendRemixMessage(music, request);

        return ApiResponse.onSuccess(MemberConverter.toTaskProgressDTO(task));
    }

    @Operation(summary = "작업 조회", description = "작업 ID를 받아 작업 상태를 조회합니다.")
    @PostMapping("/get-task")
    public ApiResponse<MemberResponseDTO.TaskProgressDTO> getTask(
            @RequestBody @Validated MemberRequestDTO.getTaskDTO request) {
        Task task = taskService.findById(request.getTaskId());
        return ApiResponse.onSuccess(MemberConverter.toTaskProgressDTO(task));
    }
}
