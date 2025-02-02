package umc.codeplay.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.domain.enums.FileType;
import umc.codeplay.dto.FileResponseDTO;
import umc.codeplay.service.FileService;

import static umc.codeplay.service.FileService.buildFilename;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(
            summary = "Upload용 Presigned URL 생성",
            description = "업로드를 위한 Presigned URL 생성 - 유효시간 존재")
    @PostMapping("/upload")
    public ApiResponse<FileResponseDTO.UploadFile> generateUrl(
            @RequestParam(value = "fileType") FileType fileType,
            @RequestParam(value = "fileName") String fileName) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newFileName = fileType.getFolderName() + buildFilename(fileName);

        Long id = fileType.processUpload(fileService, newFileName, username);
        String uploadUrl = fileService.generatePutPresignedUrl(newFileName);

        return ApiResponse.onSuccess(fileType.createResponse(uploadUrl, id));
    }
}
