package umc.codeplay.dto;

import lombok.*;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.domain.Remix;

public class SQSMessageDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HarmonyMessageDTO {
        String key;
        Long taskId;
        String jobType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackMessageDTO {
        String key;
        Long taskId;
        String jobType;

        @Setter String twoStemConfig;
    }

    @Getter
    @Setter
    public static class RemixMessageDTO {
        String key;
        Long taskId;
        String jobType;

        Integer scaleModulation;
        Double tempoRatio;
        Double reverbAmount;
        Boolean isChorusOn;

        @Builder
        public RemixMessageDTO() {
            this.scaleModulation = 0;
            this.tempoRatio = 1.0;
            this.reverbAmount = 0.0;
            this.isChorusOn = false;
        }

        public void setRemix(Remix parentRemix, MemberRequestDTO.RemixTaskDTO request) {
            boolean change = false;

            if (request.getScaleModulation() == null) {
                scaleModulation = parentRemix.getScaleModulation();
            } else {
                scaleModulation = request.getScaleModulation();
                change = (!scaleModulation.equals(parentRemix.getScaleModulation()));
            }

            if (request.getTempoRatio() == null) {
                tempoRatio = parentRemix.getTempoRatio();
            } else {
                tempoRatio = request.getTempoRatio();
                change = change || (!tempoRatio.equals(parentRemix.getTempoRatio()));
            }

            if (request.getReverbAmount() == null) {
                reverbAmount = parentRemix.getReverbAmount();
            } else {
                reverbAmount = request.getReverbAmount();
                change = change || (!reverbAmount.equals(parentRemix.getReverbAmount()));
            }

            if (request.getIsChorusOn() == null) {
                isChorusOn = parentRemix.getIsChorusOn();
            } else {
                isChorusOn = request.getIsChorusOn();
                change = change || (!isChorusOn.equals(parentRemix.getIsChorusOn()));
            }

            if (!change) {
                throw new GeneralException(ErrorStatus.REMIX_NO_CHANGE);
            }
        }

        public void setRemix(MemberRequestDTO.RemixTaskDTO request) {
            boolean change = false;

            if (request.getScaleModulation() == null) {
                scaleModulation = 0;
            } else {
                scaleModulation = request.getScaleModulation();
                change = (!scaleModulation.equals(0));
            }

            if (request.getTempoRatio() == null) {
                tempoRatio = 1.0;
            } else {
                tempoRatio = request.getTempoRatio();
                change = change || (!tempoRatio.equals(1.0));
            }

            if (request.getReverbAmount() == null) {
                reverbAmount = 0.0;
            } else {
                reverbAmount = request.getReverbAmount();
                change = change || (!reverbAmount.equals(0.0));
            }

            if (request.getIsChorusOn() == null) {
                isChorusOn = false;
            } else {
                isChorusOn = request.getIsChorusOn();
                change = change || (!isChorusOn.equals(false));
            }

            if (!change) {
                throw new GeneralException(ErrorStatus.REMIX_NO_CHANGE);
            }
        }
    }
}
