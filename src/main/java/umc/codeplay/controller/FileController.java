package umc.codeplay.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.dto.FileResponseDTO;
import umc.codeplay.service.FileService;

import static umc.codeplay.service.FileService.buildFilename;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(
            summary = "Download용 Presigned URL 생성",
            description = "다운로드를 위한 Presigned URL 생성 - 유효시간 존재")
    @GetMapping("/download")
    public ApiResponse<FileResponseDTO.DownloadFile> getUrl(
            @RequestParam(value = "musicId") Long musicId) {
        String downloadUrl = fileService.generateGetPresignedUrl(musicId);
        FileResponseDTO.DownloadFile result = new FileResponseDTO.DownloadFile(downloadUrl);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "Upload용 Presigned URL 생성",
            description = "업로드를 위한 Presigned URL 생성 - 유효시간 존재")
    @PostMapping("/upload")
    public ApiResponse<FileResponseDTO.UploadFile> generateUrl(
            @RequestParam(value = "fileName") String fileName) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newFileName = buildFilename(fileName);
        System.out.println(newFileName);
        Long musicId = fileService.uploadMusic(newFileName, username);

        String uploadUrl = fileService.generatePutPresignedUrl(newFileName);
        FileResponseDTO.UploadFile result = new FileResponseDTO.UploadFile(uploadUrl, musicId);
        return ApiResponse.onSuccess(result);
    }
}
