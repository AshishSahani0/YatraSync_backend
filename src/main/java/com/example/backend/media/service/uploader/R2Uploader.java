package com.example.backend.media.service.uploader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Component
public class R2Uploader {

    @Value("${r2.access-key}")
    private String accessKey;

    @Value("${r2.secret-key}")
    private String secretKey;

    @Value("${r2.bucket}")
    private String bucket;

    @Value("${r2.endpoint}")
    private String endpoint;

    @Value("${r2.public-url}")
    private String publicUrl;

    private S3Client s3;

    private S3Client getClient() {
        if (s3 == null) {
            s3 = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .region(Region.US_EAST_1)
                    .build();
        }
        return s3;
    }

    public String upload(MultipartFile file, String key) {

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .cacheControl("public, max-age=31536000")
                    .build();

            getClient().putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            return publicUrl.endsWith("/")
                    ? publicUrl + key
                    : publicUrl + "/" + key;

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
    public void delete(String key) {
        try {
            getClient().deleteObject(builder -> builder
                    .bucket(bucket)
                    .key(key)
            );
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
}