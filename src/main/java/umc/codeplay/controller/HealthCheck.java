package umc.codeplay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Hidden;
import umc.codeplay.service.S3Service;

@Hidden
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthCheck {

    private final S3Service s3Service;

    // 연결 확인 용
    @GetMapping("")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UMC 7th CodePlay Well Connected!");
    }

    // s3 업로드 테스트 용
    @PostMapping("/s3")
    public ResponseEntity<String> s3HealthCheck(@RequestPart(value = "file") MultipartFile file) {
        final String s3Url = s3Service.uploadFile(file);
        return ResponseEntity.ok("S3 FIle is uploaded! : " + s3Url);
    }
}
