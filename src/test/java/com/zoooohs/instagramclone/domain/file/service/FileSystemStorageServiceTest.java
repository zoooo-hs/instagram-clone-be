package com.zoooohs.instagramclone.domain.file.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileSystemStorageServiceTest extends BaseStorageServiceTest {

    private File bucket;

    @Override
    @BeforeEach
    public void setUp() {
        storageService = new FileSystemStorageServiceImpl();
        bucketName = "bucket-name" + UUID.randomUUID() + "/";
        storageService.setBucketName(bucketName);
        bucket = new File(bucketName);
        bucket.mkdir();
        super.setUp();
    }

    @Override
    @Test
    public void bucketExistTest() {
        assertTrue(bucket.exists());
    }

    @AfterEach
    public void tearDown() {
        for (File file: bucket.listFiles()) {
            file.delete();
        }
        bucket.delete();
    }
}
