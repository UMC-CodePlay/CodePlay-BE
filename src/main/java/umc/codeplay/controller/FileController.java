package umc.codeplay.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.domain.enums.FileType;
import umc.codeplay.dto.FileResponseDTO;
import umc.codeplay.service.FileService;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "file-controller", description = "모든 파일(사진, 음성)을 s3에 올릴 때 사용하는 api")
public class FileController {

    private final FileService fileService;

    @Operation(
            summary = "Upload용 Presigned URL 생성",
            description = "업로드를 위한 Presigned URL 생성 - 유효시간 존재")
    @PostMapping("/upload")
    public ApiResponse<FileResponseDTO.UploadFile> generateUrl(
            @RequestParam FileType fileType, @RequestParam String fileName) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.onSuccess(fileService.getUploadUrl(username, fileName, fileType));
    }
}
