package com.echocano.zestorder.product.infrastructure.adapter.output.storage.s3;

import com.echocano.zestorder.product.application.port.output.ImageStorageOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageStorageAdapter implements ImageStorageOutputPort {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String generateUploadUrl(String fileName, String contentType, long contentLength) {
        log.debug("generateUploadUrl: fileName = {}, contentType = {}", fileName, contentType);
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key("product/" + fileName)
                        .contentType(contentType)
                        .contentLength(contentLength))
                .build();
        String url = s3Presigner.presignPutObject(presignRequest).url().toString();
        log.debug("generateUploadUrl: presignRequest = {}", url);
        return url;
    }
}
