package umc.codeplay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.domain.*;
import umc.codeplay.domain.enums.JobType;
import umc.codeplay.domain.enums.ProcessStatus;
import umc.codeplay.dto.MemberRequestDTO;
import umc.codeplay.dto.ModelRequestDTO;
import umc.codeplay.dto.SQSMessageDTO;
import umc.codeplay.repository.HarmonyRepository;
import umc.codeplay.repository.RemixRepository;
import umc.codeplay.repository.TrackRepository;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final SqsTemplate sqsTemplate;
    private final HarmonyRepository harmonyRepository;
    private final TrackRepository trackRepository;
    private final RemixRepository remixRepository;
    private final TaskService taskService;

    @Value("${sqs.queue.name}")
    private String queueName;

    public Long setHarmony(ModelRequestDTO.HarmonyRequestDTO harmonyResult) {
        Task task =
                taskService.updateTaskStatus(harmonyResult.getTaskId(), ProcessStatus.COMPLETED);
        Harmony harmony = harmonyRepository.findByIdOrThrow(task.getJobId());

        harmony.updateHarmonyResult(
                harmonyResult.getScale(),
                harmonyResult.getBpm(),
                harmonyResult.getGenre(),
                harmonyResult.getVoiceColor());
        return harmonyRepository.save(harmony).getId();
    }

    public Long setTrack(ModelRequestDTO.TrackRequestDTO trackResult) {
        Task task = taskService.updateTaskStatus(trackResult.getTaskId(), ProcessStatus.COMPLETED);
        Track track = trackRepository.findByIdOrThrow(task.getJobId());

        track.updateTrackResult(
                trackResult.getVocalUrl(),
                trackResult.getInstrumentalUrl(),
                trackResult.getBassUrl(),
                trackResult.getDrumsUrl());
        return trackRepository.save(track).getId();
    }

    public Long setRemix(ModelRequestDTO.RemixRequestDTO remixResult) {
        Task task = taskService.updateTaskStatus(remixResult.getTaskId(), ProcessStatus.COMPLETED);
        Remix remix = remixRepository.findByIdOrThrow(task.getJobId());

        remix.setResultMusicUrl(remixResult.getResultMusicUrl());
        return remixRepository.save(remix).getId();
    }

    @Transactional
    public Task sendTrackMessage(Music music, String config) {

        Task task = taskService.addTask(music, JobType.TRACK);

        if (config == null) {
            config = "none";
        }

        switch (config) {
            case "vocals", "bass", "drums", "none": // TODO: 6-stems guitar, piano 테스트 후 추가
                break;
            default:
                throw new GeneralException(ErrorStatus.INVALID_CONFIG);
        }

        try {
            sqsTemplate.send(
                    queueName,
                    SQSMessageDTO.TrackMessageDTO.builder()
                            .key(music.getTitle())
                            .taskId(task.getId())
                            .jobType(JobType.TRACK.toString())
                            .twoStemConfig(config)
                            .build());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SQS_SEND_ERROR);
        }
        return task;
    }

    @Transactional
    public Task sendHarmonyMessage(Music music) {

        Task task = taskService.addTask(music, JobType.HARMONY);

        try {
            sqsTemplate.send(
                    queueName,
                    SQSMessageDTO.HarmonyMessageDTO.builder()
                            .key(music.getTitle())
                            .taskId(task.getId())
                            .jobType(JobType.HARMONY.toString())
                            .build());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SQS_SEND_ERROR);
        }
        return task;
    }

    @Transactional
    public Task sendRemixMessage(Music music, MemberRequestDTO.RemixTaskDTO request) {

        Remix parentRemix = null;
        SQSMessageDTO.RemixMessageDTO remixPayLoad =
                SQSMessageDTO.RemixMessageDTO.builder().build();

        if (request.getParentRemixId() != null) {
            parentRemix = remixRepository.findByIdOrThrow(request.getParentRemixId());
            if (!parentRemix.getMusic().getId().equals(music.getId())) {
                throw new GeneralException(ErrorStatus.INVALID_PARENT_REMIX);
            }
            remixPayLoad.setRemix(parentRemix, request);
        } else {
            remixPayLoad.setRemix(request);
        }

        Remix newRemix =
                remixRepository.save(
                        Remix.builder()
                                .music(music)
                                .scaleModulation(remixPayLoad.getScaleModulation())
                                .tempoRatio(remixPayLoad.getTempoRatio())
                                .reverbAmount(remixPayLoad.getReverbAmount())
                                .isChorusOn(remixPayLoad.getIsChorusOn())
                                .build());

        if (parentRemix != null) {
            newRemix.setParentRemix(parentRemix);
            parentRemix.getChildRemixList().add(newRemix);
            remixRepository.save(parentRemix);
        }

        Task task = taskService.addTask(newRemix);

        remixPayLoad.setKey(music.getTitle());
        remixPayLoad.setTaskId(task.getId());
        remixPayLoad.setJobType(JobType.REMIX.toString());

        try {
            sqsTemplate.send(queueName, remixPayLoad);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SQS_SEND_ERROR);
        }
        return task;
    }
}
