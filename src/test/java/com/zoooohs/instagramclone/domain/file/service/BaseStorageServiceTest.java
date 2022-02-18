package com.zoooohs.instagramclone.domain.file.service;

import com.zoooohs.instagramclone.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class BaseStorageServiceTest {
    protected StorageService storageService;
    protected List<MultipartFile> files;
    protected String bucketName;

    private MockMultipartFile file;
    private String path;

    public void setUp() {
        files = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
        file = new MockMultipartFile("files", "file_name.txt",
                MediaType.TEXT_PLAIN_VALUE, "contents".getBytes());

        path = storageService.store(List.of(file)).get(0);
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

    @DisplayName("path 넘겨서 오브젝트 존재 유무 boolean 반환하는 서비스")
    @Test
    public void existsTest() {
        String notExistPath = "nope";
        String someExistPath = path;

        Boolean expectedFalse = storageService.exists(notExistPath);
        Boolean expectedTrue = storageService.exists(someExistPath);

        assertFalse(expectedFalse);
        assertTrue(expectedTrue);

        // tear down
        File f = new File(someExistPath);
        if (f.exists()) {
            f.delete();
        }
    }

    @DisplayName("storage service에 path 넘겨서 오브젝트 삭제")
    @Test
    public void deleteTest() {
        storageService.delete(path);
        Boolean exists = storageService.exists(path);

        assertFalse(exists);
    }

    @DisplayName("여러 경로를 넘기면 한번의 bulk 삭제 서비스")
    @Test
    public void deleteAllTest() {
        List<String> paths = storageService.store(files);

        storageService.deleteAll(paths);

        for (String path: paths) {
            assertFalse(storageService.exists(path));
        }
    }

    public void bucketExistTest() {}
}
