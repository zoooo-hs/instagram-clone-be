package com.zoooohs.instagramclone.domain.file.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class S3StorageServiceTest extends BaseStorageServiceTest {

    private S3Mock api;

    @Override
    @BeforeEach
    public void setUp() {
        api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
        api.start();
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "ap-northeast-2");
        AmazonS3 amazonS3Client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        String bucketName = "bucket-name";
        amazonS3Client.createBucket(bucketName);
        storageService = new S3StorageServiceImpl(amazonS3Client);
        storageService.setBucketName(bucketName);
        super.setUp();
    }

    @AfterEach
    public void tearDown() {
        api.shutdown();
    }

}
