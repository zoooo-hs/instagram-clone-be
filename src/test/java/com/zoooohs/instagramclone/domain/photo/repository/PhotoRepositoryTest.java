package com.zoooohs.instagramclone.domain.photo.repository;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PhotoRepositoryTest {

    @Autowired
    PhotoRepository photoRepository;

    @DisplayName("photo repository saveAll 테스트")
    @Test
    public void saveAllTest() {
        List<PhotoEntity> photoEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPath(UUID.randomUUID().toString());
            photoEntities.add(photoEntity);
        }

        List<PhotoEntity> actual = this.photoRepository.saveAll(photoEntities);

        for (PhotoEntity photo: actual) {
            assertTrue(photo.getId() != null);
        }
    }
}
