package umc.codeplay.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import software.amazon.awssdk.http.SdkHttpMethod;
import umc.codeplay.apiPayLoad.ApiResponse;
import umc.codeplay.dto.FileResponseDTO;
import umc.codeplay.service.FileService;

import static umc.codeplay.service.FileService.buildFilename;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Download용 Presigned URL 생성", description = "다운로드를 위한 Presigned URL 생성")
    @GetMapping("/presigned/download")
    public ApiResponse<FileResponseDTO.DownloadFile> getUrl(
            @RequestParam(value = "filename") String fileName) {
        String preSignedUrl = fileService.generatePreSignedUrl(fileName, SdkHttpMethod.GET);
        FileResponseDTO.DownloadFile result = new FileResponseDTO.DownloadFile(preSignedUrl);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "Upload용 Presigned URL 생성", description = "업로드를 위한 Presigned URL 생성")
    @PostMapping("/presigned/upload")
    public ApiResponse<FileResponseDTO.UploadFile> generateUrl(
            @RequestParam(value = "filename") String fileName) {
        String newFileName = buildFilename(fileName);
        String url = fileService.generatePreSignedUrl(newFileName, SdkHttpMethod.PUT);
        FileResponseDTO.UploadFile result = new FileResponseDTO.UploadFile(newFileName, url);

        return ApiResponse.onSuccess(result);
    }
}
