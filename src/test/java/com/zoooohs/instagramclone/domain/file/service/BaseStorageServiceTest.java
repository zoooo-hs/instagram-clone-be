package com.zoooohs.instagramclone.domain.file.service;

import com.zoooohs.instagramclone.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseStorageServiceTest {
    protected StorageService storageService;
    protected List<MultipartFile> files;
    protected String bucketName;

    public void setUp() {
        files = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
    }

    @DisplayName("multi part storage에 저장후 경로 받아오기")
    @Test
    public void storeTest() {
        List<String> actual = storageService.store(files);

        assertEquals(files.size(), actual.size());
        for (int i = 0; i < files.size(); i++) {
            String path = actual.get(i);
            assertEquals(FileUtils.getExtension(files.get(i).getOriginalFilename()), FileUtils.getExtension(path));
        }
    }

    public void bucketExistTest() {}
}
