package umc.codeplay.service;

import java.text.Normalizer;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.repository.MemberRepository;
import umc.codeplay.repository.MusicRepository;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private final S3Presigner s3Presigner;
    private final MusicRepository musicRepository;
    private final MemberRepository memberRepository;

    // 타임스탬프_파일명 형식으로 파일 이름 저장
    public static String buildFilename(String filename) {
        return String.format("%s_%s", System.currentTimeMillis(), sanitizeFileName(filename));
    }

    // 특수 문자나 공백 등을 정리
    private static String sanitizeFileName(String fileName) {
        String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);
        System.out.println(normalizedFileName);
        return normalizedFileName.replaceAll("\\s+", "_").replaceAll("[^가-힣a-zA-Z0-9.\\-_]", "_");
    }

    // S3에서 파일을 다운로드할 수 있는 Presigned URL 생성
    public String generateGetPresignedUrl(Long musicId) {
        try {
            Music music =
                    musicRepository
                            .findById(musicId)
                            .orElseThrow(() -> new GeneralHandler(ErrorStatus.MUSIC_NOT_FOUND));

            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(music.getTitle()).build();

            GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(60))
                            .getObjectRequest(getObjectRequest)
                            .build();

            PresignedGetObjectRequest presignedRequest =
                    s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.AWS_SERVICE_UNAVAILABLE);
        }
    }

    // S3에 파일을 업로드할 수 있는 Presigned URL 생성
    public String generatePutPresignedUrl(String fileName) {
        try {
            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(60))
                            .putObjectRequest(putObjectRequest)
                            .build();

            PresignedPutObjectRequest presignedRequest =
                    s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.AWS_SERVICE_UNAVAILABLE);
        }
    }

    // music 레포지토리에 업로드
    public Long uploadMusic(String newFileName, String userEmail) {
        Member member =
                memberRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 저장하는 url은 유효시간이 없는 public
        // TODO: 업로드에만 presigned 사용할지 아님 다운로드시에도 사용할지에 따라 변경해야함.
        String s3Url =
                String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, newFileName);
        Music newMusic = Music.builder().title(newFileName).musicUrl(s3Url).member(member).build();

        return musicRepository.save(newMusic).getId();
    }

    // TODO: 필요시 직접 업로드 방법 구현 필요
}
