package umc.codeplay.domain.enums;

import umc.codeplay.dto.FileResponseDTO;
import umc.codeplay.service.FileService;

public enum FileType {
    AUDIO {
        public String getFolderName() {
            return "requestFiles/";
        }

        public Long processUpload(FileService fileService, String fileName, String username) {
            return fileService.uploadMusic(fileName, username);
        }

        public FileResponseDTO.UploadFile createResponse(String uploadUrl, Long id) {
            return new FileResponseDTO.UploadFile(uploadUrl, id, null);
        }
    },
    IMAGE {
        public String getFolderName() {
            return "profileImgs/";
        }

        public Long processUpload(FileService fileService, String fileName, String username) {
            return fileService.uploadProfile(fileName, username);
        }

        public FileResponseDTO.UploadFile createResponse(String uploadUrl, Long id) {
            return new FileResponseDTO.UploadFile(uploadUrl, null, id);
        }
    };

    public abstract String getFolderName();

    public abstract Long processUpload(FileService fileService, String fileName, String username);

    public abstract FileResponseDTO.UploadFile createResponse(String uploadUrl, Long id);
}
