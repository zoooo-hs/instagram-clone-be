package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.file.service.FileSystemStorageServiceImpl;
import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.photo.repository.PhotoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {

    PhotoService photoService;

    StorageService storageService;

    @Mock
    PhotoRepository photoRepository;

    @Spy
    ModelMapper modelMapper;

    private List<MultipartFile> files;
    List<String> photoIds;

    @BeforeEach
    public void setUp() {
        storageService = new FileSystemStorageServiceImpl();
        photoService = new PhotoServiceImpl(photoRepository, modelMapper);
        files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", UUID.randomUUID().toString(),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
        photoIds = storageService.store(files);
    }

    @AfterEach
    public void tearDown() {
        for (String id: photoIds) {
            new File(id).delete();
        }
    }


    @DisplayName("path혹은 식별자를 File 관련 Entity에 저장")
    @Test
    public void saveAllTest() {
        List<PhotoEntity> photoEntities = new ArrayList<>();
        for (int i = 0; i < photoIds.size(); i++) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPath(photoIds.get(i));
            photoEntity.setId((long) i);
            photoEntities.add(photoEntity);
        }

        given(photoRepository.saveAll(anyList())).willReturn(photoEntities);

        List<PhotoDto.Photo> actual = this.photoService.saveAll(photoIds);

        assertEquals(photoIds.size(), actual.size());
        for (PhotoDto.Photo photo: actual) {
            assertTrue(photo.getId() != null);
        }
    }
}
