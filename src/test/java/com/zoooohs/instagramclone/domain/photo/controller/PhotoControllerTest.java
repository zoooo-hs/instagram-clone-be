package com.zoooohs.instagramclone.domain.photo.controller;

import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.service.PhotoService;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;


import static org.mockito.BDDMockito.*;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = PhotoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class PhotoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PhotoService photoService;

    @DisplayName("PATCH /user/{userId}/photo, multipart photo, jwt 입력 받아 User Info json 반환. jpg, png가 아닌경우 400, userId와 jwt가 일치하지 않는 경우 404 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void uploadProfileTest() throws Exception {
        String url = String.format("/user/%s/photo", 1L);
        String url404 = String.format("/user/%s/photo", 2L);

        MockMultipartFile imageFile = new MockMultipartFile("photo", "original_name.jpg", MediaType.IMAGE_JPEG_VALUE, "image_content".getBytes());

        PhotoDto.Photo photo = PhotoDto.Photo.builder().id(1L).path("some path").build();

        given(photoService.uploadProfile(any(MultipartFile.class), eq(1L))).willReturn(photo);

        mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                        .file(imageFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.instanceOf(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.instanceOf(Integer.class)))
        ;

        mockMvc.perform(MockMvcRequestBuilders.multipart(url404)
                        .file(imageFile))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", Matchers.is(ErrorCode.USER_NOT_FOUND.name())))
        ;
    }

    @DisplayName("PATCH /user/{userId}/photo, multipart photo, jwt 입력 받아 jpg, png가 아닌경우 400")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void uploadProfileFailure400Test() throws Exception {
        String url = String.format("/user/%s/photo", 1L);

        MockMultipartFile nonImageFile = new MockMultipartFile("photo", "original_name.txt", MediaType.TEXT_PLAIN_VALUE, "text_content".getBytes());

        given(photoService.uploadProfile(any(MultipartFile.class), eq(1L))).willThrow(new ZooooException(ErrorCode.INVALID_FILE_TYPE));

        mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                        .file(nonImageFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", Matchers.is(ErrorCode.INVALID_FILE_TYPE.name())))
        ;
    }
}
