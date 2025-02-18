package umc.codeplay.domain.enums;

import umc.codeplay.dto.FileResponseDTO;

public enum FileType {
    AUDIO {
        @Override
        public String buildStoragePath(Long id, String fileName) {
            return String.format("%s%d/%s", BASE_AUDIO_PATH, id, fileName);
        }
    },
    IMAGE {
        @Override
        public String buildStoragePath(Long id, String fileName) {
            return String.format("%s%d/%s", BASE_IMAGE_PATH, id, fileName);
        }
    };

    private static final String BASE_AUDIO_PATH = "requestFiles/";
    private static final String BASE_IMAGE_PATH = "profileImgs/";

    public abstract String buildStoragePath(Long id, String fileName);

    public FileResponseDTO.UploadFile createResponse(String uploadUrl, Long id) {
        return this == AUDIO
                ? new FileResponseDTO.UploadFile(uploadUrl, id, null)
                : new FileResponseDTO.UploadFile(uploadUrl, null, id);
    }
}
