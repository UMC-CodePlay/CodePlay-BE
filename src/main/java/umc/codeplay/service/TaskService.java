package umc.codeplay.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.converter.TaskConverter;
import umc.codeplay.domain.*;
import umc.codeplay.domain.enums.JobType;
import umc.codeplay.domain.enums.ProcessStatus;
import umc.codeplay.dto.TaskResponseDTO;
import umc.codeplay.repository.HarmonyRepository;
import umc.codeplay.repository.RemixRepository;
import umc.codeplay.repository.TaskRepository;
import umc.codeplay.repository.TrackRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final RemixRepository remixRepository;
    private final TrackRepository trackRepository;
    private final HarmonyRepository harmonyRepository;
    private final TaskConverter taskConverter;

    public Task findById(Long id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TASK_NOT_FOUND));
    }

    public Task removeTask(Long id) {
        Task task = findById(id);
        // TODO: 정크 엔티티 삭제
        taskRepository.delete(task);
        return task;
    }

    public Task updateTaskStatus(Long id, ProcessStatus status) {
        Task task = findById(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task addTask(Music music, JobType jobType) {
        Long jobId =
                switch (jobType) {
                    case HARMONY -> harmonyRepository
                            .save(Harmony.builder().music(music).build())
                            .getId();
                    case TRACK -> trackRepository
                            .save(Track.builder().music(music).build())
                            .getId();
                    default -> throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
                };

        Task task =
                Task.builder()
                        .status(ProcessStatus.REQUESTED)
                        .jobType(jobType)
                        .jobId(jobId)
                        .build();
        return taskRepository.save(task);
    }

    public Task addTask(Remix newRemix) {
        Task task =
                Task.builder()
                        .status(ProcessStatus.REQUESTED)
                        .jobType(JobType.REMIX)
                        .jobId(newRemix.getId())
                        .build();

        return taskRepository.save(task);
    }

    public Task waitTask(Long id, long timeoutMillis) {
        Instant startTime = Instant.now();
        Task task = findById(id);

        while (Duration.between(startTime, Instant.now()).toMillis() < timeoutMillis) {

            // complete 면 응답
            if (task.getStatus().equals(ProcessStatus.COMPLETED)) {
                return task;
            }

            // 3초 대기이후 다시 작업 체크
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
            }
        }

        return findById(id);
    }

    public TaskResponseDTO.GetByTaskIdDTO findByTaskId(Long taskId) {
        // task 객체 찾기
        Task task =
                taskRepository
                        .findById(taskId)
                        .orElseThrow(() -> new RuntimeException("Task를 찾을 수 없습니다."));

        List<TaskResponseDTO.HarmonyDTO> harmonyDTOs = new ArrayList<>();
        List<TaskResponseDTO.TrackDTO> trackDTOs = new ArrayList<>();
        List<TaskResponseDTO.RemixDTO> remixDTOs = new ArrayList<>();

        // job_type에 따라 해당 객체 조회 후 DTO 반환
        switch (task.getJobType()) {
            case HARMONY:
                Harmony harmony =
                        harmonyRepository
                                .findById(task.getJobId())
                                .orElseThrow(() -> new RuntimeException("Harmony를 찾을 수 없습니다."));

                harmonyDTOs.add(taskConverter.toHarmonyDTO(harmony));
                break;
            case TRACK:
                Track track =
                        trackRepository
                                .findById(task.getJobId())
                                .orElseThrow(() -> new RuntimeException("Track을 찾을 수 없습니다."));
                trackDTOs.add(taskConverter.toTrackDTO(track));
                break;
            case REMIX:
                Remix remix =
                        remixRepository
                                .findById(task.getJobId())
                                .orElseThrow(() -> new RuntimeException("Remix를 찾을 수 없습니다."));
                remixDTOs.add(taskConverter.toRemixDTO(remix));
                break;
        }

        // DTO를 하나의 객체로 묶어서 반환
        return new TaskResponseDTO.GetByTaskIdDTO(harmonyDTOs, trackDTOs, remixDTOs);
    }
}
