package umc.codeplay.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

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

    @GetMapping("download/{fileName}")
    public ApiResponse<FileResponseDTO.DownloadFile> getUrl(@PathVariable String fileName) {
        String preSignedUrl = fileService.generatePreSignedUrl(fileName, SdkHttpMethod.GET);
        FileResponseDTO.DownloadFile result = new FileResponseDTO.DownloadFile(preSignedUrl);

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("upload/{fileName}")
    public ApiResponse<FileResponseDTO.UploadFile> generateUrl(@PathVariable String fileName) {
        String newFileName = buildFilename(fileName);
        String url = fileService.generatePreSignedUrl(newFileName, SdkHttpMethod.PUT);
        FileResponseDTO.UploadFile result = new FileResponseDTO.UploadFile(newFileName, url);

        return ApiResponse.onSuccess(result);
    }
}
