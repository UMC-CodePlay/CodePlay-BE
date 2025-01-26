package umc.codeplay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthCheck {

    // 연결 확인 용
    @GetMapping("")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UMC 7th CodePlay Well Connected!");
    }
}
