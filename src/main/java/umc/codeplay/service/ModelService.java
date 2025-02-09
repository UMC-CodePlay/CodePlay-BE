package umc.codeplay.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Remix;
import umc.codeplay.domain.Task;
import umc.codeplay.domain.Track;
import umc.codeplay.dto.ModelRequestDTO;
import umc.codeplay.repository.HarmonyRepository;
import umc.codeplay.repository.RemixRepository;
import umc.codeplay.repository.TaskRepository;
import umc.codeplay.repository.TrackRepository;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final TaskRepository taskRepository;
    private final HarmonyRepository harmonyRepository;
    private final TrackRepository trackRepository;
    private final RemixRepository remixRepository;

    public Long setHarmony(ModelRequestDTO.HarmonyRequestDTO harmonyResult) {
        Task task = taskRepository.findByIdOrThrow(harmonyResult.getTaskId());
        Harmony harmony = harmonyRepository.findByIdOrThrow(task.getJobId());

        harmony.updateHarmonyResult(
                harmonyResult.getScale(),
                harmonyResult.getBpm(),
                harmonyResult.getGenre(),
                harmonyResult.getVoiceColor());
        return harmonyRepository.save(harmony).getId();
    }

    public Long setTrack(ModelRequestDTO.TrackRequestDTO trackResult) {
        Task task = taskRepository.findByIdOrThrow(trackResult.getTaskId());
        Track track = trackRepository.findByIdOrThrow(task.getJobId());

        track.updateTrackResult(
                trackResult.getVocalUrl(),
                trackResult.getInstrumentalUrl(),
                trackResult.getBassUrl(),
                trackResult.getDrumsUrl());
        return trackRepository.save(track).getId();
    }

    public Long setRemix(ModelRequestDTO.RemixRequestDTO remixResult) {
        Task task = taskRepository.findByIdOrThrow(remixResult.getTaskId());
        Remix remix = remixRepository.findByIdOrThrow(task.getJobId());

        remix.setResultMusicUrl(remixResult.getResultMusicUrl());
        return remixRepository.save(remix).getId();
    }
}
