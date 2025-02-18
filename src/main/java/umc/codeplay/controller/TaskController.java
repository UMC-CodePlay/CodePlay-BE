package umc.codeplay.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.converter.MemberConverter;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.Task;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.MemberResponseDTO;
import umc.codeplay.dto.TaskResponseDTO;
import umc.codeplay.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

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

    @Hidden // TODO: 기능 완성시 @Hidden 태그만 삭제해주세요!
    @Operation(
            summary = "작업 진행 상황 조회",
            description = "작업 ID를 받아 완료될 때까지 기다린 후 결과를 반환합니다. 기본 대기시간 5분, 10초마다 작업 상태확인.")
    @GetMapping("/wait/{taskId}")
    public ApiResponse<MemberResponseDTO.TaskProgressDTO> waitTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "300000") long timeoutMillis // 기본 5분 대기시간 + 10초마다 작업상태 체크
            ) {
        Task task = taskService.waitTask(taskId, timeoutMillis);
        return ApiResponse.onSuccess(MemberConverter.toTaskProgressDTO(task));
    }

    @Operation(
            summary = "task id로 harmony, track, remix 조회",
            description = "task id를 query parameter로 넘기고 그에 맞는 객체를 반환합니다.")
    @GetMapping("/search")
    public ApiResponse<TaskResponseDTO.GetByTaskIdDTO> getByTaskId(@RequestParam Long taskId) {

        System.out.println("테스트중입니다");

        TaskResponseDTO.GetByTaskIdDTO responseDTO = taskService.findByTaskId(taskId);

        return ApiResponse.onSuccess(responseDTO);
    }
}
