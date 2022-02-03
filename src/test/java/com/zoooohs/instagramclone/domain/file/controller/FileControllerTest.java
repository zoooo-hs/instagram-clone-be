package com.zoooohs.instagramclone.domain.file.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.domain.file.service.FileSystemStorageServiceImpl;
import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.service.PhotoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = FileController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith({MockitoExtension.class})
public class FileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StorageService storageService;

    @MockBean
    PhotoService photoService;

    ObjectMapper objectMapper;
    private List<MultipartFile> files;
    private MockMultipartHttpServletRequestBuilder multiPartRequestBody;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }

        String postUrl = "/file";
        multiPartRequestBody = MockMvcRequestBuilders.multipart(postUrl);
        files.stream().forEach(file -> multiPartRequestBody.file((MockMultipartFile) file));
    }

    @DisplayName("POST /file 로 MultipartFile List 받아, Photo List 반환. 같은 길이")
    @Test
    public void inMultiFilesOutStringsTest() throws Exception {
        int fileCount = files.size();

        ArrayList<String> ids = new ArrayList<>();
        ArrayList<PhotoDto.Photo> photoDtos = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            ids.add(UUID.randomUUID().toString());
            photoDtos.add(new PhotoDto.Photo());
        }

        given(this.storageService.store(eq(files))).willReturn(ids);
        given(this.photoService.saveAll(eq(ids))).willReturn(photoDtos);

        mockMvc.perform(multiPartRequestBody)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(fileCount)));
    }
}
