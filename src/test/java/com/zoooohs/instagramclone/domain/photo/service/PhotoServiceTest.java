package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.file.service.FileSystemStorageServiceImpl;
import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.photo.repository.PhotoRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = { "cloud.aws.s3.bucket=bucket" })
public class PhotoServiceTest {

    PhotoService photoService;

    StorageService storageService;

    @Mock
    PhotoRepository photoRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    ModelMapper modelMapper;

    private List<MultipartFile> files;
    List<String> photoIds;
    private List<PhotoEntity> photoEntities;

    @BeforeEach
    public void setUp() {
        storageService = new FileSystemStorageServiceImpl();
        photoService = new PhotoServiceImpl(photoRepository, userRepository, storageService, modelMapper);
        files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", UUID.randomUUID().toString(),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
        photoIds = storageService.store(files);
        photoEntities = new ArrayList<>();
        for (int i = 0; i < photoIds.size(); i++) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPath(photoIds.get(i));
            photoEntity.setId((long) i);
            photoEntities.add(photoEntity);
        }
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
        given(photoRepository.saveAll(anyList())).willReturn(photoEntities);

        List<PhotoDto.Photo> actual = this.photoService.saveAll(photoIds);

        assertEquals(photoIds.size(), actual.size());
        for (PhotoDto.Photo photo: actual) {
            assertTrue(photo.getId() != null);
        }
    }

    @DisplayName("compareTo Test")
    @Test
    void compareToTest() {
       photoEntities.sort(new Comparator<PhotoEntity>() {
           @Override
           public int compare(PhotoEntity o1, PhotoEntity o2) {
               return o1.getId().compareTo(o2.getId());
           }
       });

       assertTrue(photoEntities.get(0).getId() < photoEntities.get(1).getId());
    }

    @DisplayName("userId, Multipartfile 받아 photo 저장, photo entity 저장, user entity에 photo 연결 후 photo 반환")
    @Test
    public void uploadProfileTet() {
        MockMultipartFile oldPhoto = new MockMultipartFile("photo", "old_name.jpg", MediaType.IMAGE_JPEG_VALUE, "image_content".getBytes());
        String oldPath = storageService.store(List.of(oldPhoto)).get(0);

        MockMultipartFile photo = new MockMultipartFile("photo", "original_name.jpg", MediaType.IMAGE_JPEG_VALUE, "image_content".getBytes());
        String newPath = "nulloriginal_name.jpg";
        Long userId = 1L;

        UserEntity user = UserEntity.builder().id(1L).build();

        PhotoEntity oldPhotoEntity = new PhotoEntity();
        oldPhotoEntity.setPath(oldPath);
        user.setPhoto(oldPhotoEntity);
        PhotoEntity newPhotoEntity = new PhotoEntity();
        newPhotoEntity.setId(111L);
        newPhotoEntity.setPath(newPath);

        photoIds.add(newPath); // for tear down
        photoIds.add(oldPath); // for tear down

        given(userRepository.findById((1L))).willReturn(Optional.ofNullable(user));
        given(photoRepository.saveAll(anyList())).willReturn(List.of(newPhotoEntity));


        PhotoDto.Photo actual = photoService.uploadProfile(photo, userId);


        assertNotNull(actual);
        assertEquals(newPhotoEntity.getId(), actual.getId());
        assertEquals(newPhotoEntity.getPath(), actual.getPath());
        assertTrue(storageService.exists(actual.getPath()));
        assertFalse(storageService.exists(oldPath));
    }

    @DisplayName("multipart가 jpg, png아닐 경우 INVALID_FILE_TYPE throw")
    @Test
    public void uploadProfileFailure400Tet() {
        MockMultipartFile photo = new MockMultipartFile("photo", "original_name.jpg", MediaType.TEXT_PLAIN_VALUE, "image_content".getBytes());
        Long userId = 1L;

        try {
            photoService.uploadProfile(photo, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.INVALID_FILE_TYPE, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }
}
