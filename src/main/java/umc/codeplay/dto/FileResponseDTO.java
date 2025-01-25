package umc.codeplay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class FileResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class DownloadFile {
        private String s3URL;
    }

    @Getter
    @AllArgsConstructor
    public static class UploadFile {
        private String newFileName;
        private String s3URL;
    }
}
