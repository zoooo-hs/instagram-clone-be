package com.zoooohs.instagramclone.domain.file.service;

import com.zoooohs.instagramclone.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class FileSystemStorageServiceTest {

    FileSystemStorageServiceImpl fileSystemStorageService;

    private List<MultipartFile> files;

    @BeforeEach
    public void setUp() {
        fileSystemStorageService = new FileSystemStorageServiceImpl();

        files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
    }

    @DisplayName("path 지정후 MultipartFile에 있는 내용 file system(혹은 외부 storage에) 저장")
    @Test
    public void storeTest() {
        List<String> actual = fileSystemStorageService.store(files);

        assertEquals(files.size(), actual.size());
        for (int i = 0; i < files.size(); i++) {
            String path = actual.get(i);
            assertEquals(FileUtils.getExtension(files.get(i).getOriginalFilename()), FileUtils.getExtension(path));
            assertNotEquals(fileSystemStorageService.getStoragePath() + files.get(i).getOriginalFilename(), path);
            assertTrue(new File(path).exists());
            new File(path).delete();
        }
    }
}
