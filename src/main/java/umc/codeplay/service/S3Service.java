package umc.codeplay.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    /*
      s3에 파일 업로드
    */
    public String uploadFile(MultipartFile file) {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }

        final String fileName = newFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            /*
             TODO: 용량 문제가 생긴다면 아래 ByteArrayInputStream 방식을 변경해야함
               1. 임시파일 방식으로 변경
               2. 가능한 용량 limit 설정
            */
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);

            inputStream.close();

        } catch (AmazonClientException | IOException e) {
            throw new GeneralHandler(ErrorStatus.AWS_SERVICE_UNAVAILABLE);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /*
      s3 업로드시 파일 이름 변경
    */
    private String newFileName(String fileName) {

        final String FILE_EXTENSION_SEPARATOR = ".";
        final String now = String.valueOf(System.currentTimeMillis());

        int fileExtensionIndex = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (fileExtensionIndex == -1) {
            return fileName
                    + "_"
                    + System.currentTimeMillis(); // No extension found, just add timestamp
        }

        final String fileExtension = fileName.substring(fileExtensionIndex);
        final String originalFileName = fileName.substring(0, fileExtensionIndex);

        return originalFileName + "_" + now + fileExtension;
    }
}
