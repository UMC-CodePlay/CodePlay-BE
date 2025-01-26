package umc.codeplay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class FileResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class DownloadFile {
        private String downloadS3Url;
    }

    @Getter
    @AllArgsConstructor
    public static class UploadFile {
        private String uploadS3Url;
        private Long musicId;
    }
}
