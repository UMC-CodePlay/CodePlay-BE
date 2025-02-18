package umc.codeplay.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.domain.*;
import umc.codeplay.domain.enums.JobType;
import umc.codeplay.domain.enums.ProcessStatus;
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
}
