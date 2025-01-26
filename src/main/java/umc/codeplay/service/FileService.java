package umc.codeplay.service;

import java.text.Normalizer;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Presigner s3Presigner;

    // 타임스탬프_파일명 형식으로 파일 이름 저장
    public static String buildFilename(String filename) {
        return String.format("%s_%s", System.currentTimeMillis(), sanitizeFileName(filename));
    }

    // 특수 문자나 공백 등을 정리
    private static String sanitizeFileName(String fileName) {
        String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);
        return normalizedFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9.\\-_]", "");
    }

    // 파일 업로드(HTTP PUT) 또는 다운로드(HTTP GET)를 위한 Presigned URL 생성
    public String generatePreSignedUrl(String fileName, SdkHttpMethod method) {

        return switch (method) {
            case GET -> generateGetPresignedUrl(fileName);
            case PUT -> generatePutPresignedUrl(fileName);
            default -> throw new GeneralHandler(ErrorStatus.AWS_SERVICE_UNAVAILABLE);
        };
    }

    // S3에서 파일을 다운로드할 수 있는 Presigned URL 생성
    private String generateGetPresignedUrl(String fileName) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(60))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    // S3에 파일을 업로드할 수 있는 Presigned URL 생성
    private String generatePutPresignedUrl(String fileName) {
        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(60))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    // TODO: 필요시 직접 업로드 방법 구현 필요
}
