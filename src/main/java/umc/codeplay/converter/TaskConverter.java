package umc.codeplay.converter;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import umc.codeplay.domain.Harmony;
import umc.codeplay.domain.Remix;
import umc.codeplay.domain.Track;
import umc.codeplay.dto.TaskResponseDTO;

@Component
@RequiredArgsConstructor
public class TaskConverter {

    public TaskResponseDTO.HarmonyDTO toHarmonyDTO(Harmony harmony) {

        return TaskResponseDTO.HarmonyDTO.builder()
                .harmonyId(harmony.getId())
                .musicId(harmony.getMusic().getId())
                .musicTitle(harmony.getMusic().getTitle())
                .createdAt(harmony.getCreatedAt())
                .scale(harmony.getScale())
                .genre(harmony.getGenre())
                .bpm(harmony.getBpm())
                .voiceColor(harmony.getVoiceColor())
                .build();
    }

    public TaskResponseDTO.TrackDTO toTrackDTO(Track track) {

        return TaskResponseDTO.TrackDTO.builder()
                .trackId(track.getId())
                .musicId(track.getMusic().getId())
                .musicTitle(track.getMusic().getTitle())
                .createdAt(track.getCreatedAt())
                .vocalUrl(track.getVocalUrl())
                .instrumentalUrl(track.getInstrumentalUrl())
                .bassUrl(track.getBassUrl())
                .drumsUrl(track.getDrumsUrl())
                .build();
    }

    public TaskResponseDTO.RemixDTO toRemixDTO(Remix remix) {

        return TaskResponseDTO.RemixDTO.builder()
                .remixId(remix.getId())
                .musicId(remix.getMusic().getId())
                .musicTitle(remix.getMusic().getTitle())
                .createdAt(remix.getCreatedAt())
                .scaleModulation(remix.getScaleModulation())
                .tempoRatio(remix.getTempoRatio())
                .reverbAmount(remix.getReverbAmount())
                .isCorusOn(remix.getIsChorusOn())
                .resultMusicUrl(remix.getResultMusicUrl())
                .parentRemixId(
                        remix.getParentRemix() != null
                                ? remix.getParentRemix().getId()
                                : null) // 부모 Remix ID
                .childRemixList(
                        remix.getChildRemixList() != null
                                ? remix.getChildRemixList().stream()
                                        .map(this::toRemixDTO)
                                        .collect(Collectors.toList())
                                : new ArrayList<>()) // 자식 Remix 리스트
                .build();
    }
}
