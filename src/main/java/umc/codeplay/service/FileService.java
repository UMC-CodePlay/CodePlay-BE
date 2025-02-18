package umc.codeplay.service;

import java.text.Normalizer;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Member;
import umc.codeplay.domain.Music;
import umc.codeplay.domain.enums.FileType;
import umc.codeplay.dto.FileResponseDTO;
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

    @Transactional
    public FileResponseDTO.UploadFile getUploadUrl(
            String username, String fileName, FileType fileType) {
        String sanitizedFileName = sanitizeFileName(fileName);
        Long entityId = createFileEntity(username, sanitizedFileName, fileType);
        String storagePath = fileType.buildStoragePath(entityId, sanitizedFileName);

        updateEntityUrl(entityId, storagePath, fileType);
        String uploadUrl = generatePresignedUrl(storagePath);
        return fileType.createResponse(uploadUrl, entityId);
    }

    private String sanitizeFileName(String fileName) {
        return Normalizer.normalize(fileName, Normalizer.Form.NFC)
                .replaceAll("[^가-힣a-zA-Z0-9.\\s\\-_]", "_");
    }

    private Long createFileEntity(String userEmail, String fileName, FileType fileType) {
        Member member =
                memberRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (fileType == FileType.IMAGE) {
            return member.getId();
        }

        Music newMusic = Music.builder().title(fileName).member(member).build();
        return musicRepository.save(newMusic).getId();
    }

    private void updateEntityUrl(Long id, String storagePath, FileType fileType) {
        String url =
                String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, storagePath);

        if (fileType == FileType.IMAGE) {
            Member member =
                    memberRepository
                            .findById(id)
                            .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
            member.setProfileUrl(url);
            memberRepository.save(member);
        } else {
            Music music =
                    musicRepository
                            .findById(id)
                            .orElseThrow(() -> new GeneralHandler(ErrorStatus.MUSIC_NOT_FOUND));
            music.setMusicUrl(url);
            musicRepository.save(music);
        }
    }

    private String generatePresignedUrl(String storagePath) {
        try {
            PutObjectRequest objectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(storagePath).build();

            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(60))
                            .putObjectRequest(objectRequest)
                            .build();

            return s3Presigner.presignPutObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.AWS_SERVICE_UNAVAILABLE);
        }
    }
}
