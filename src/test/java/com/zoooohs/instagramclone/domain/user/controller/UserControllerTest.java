package com.zoooohs.instagramclone.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.service.UserService;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @DisplayName("GET /user?keyword,seach_key,index,size 입력 받아 user list 반환")
    @Test
    public void searchUserTest() throws Exception {
        String url = "/user";

        List<UserDto.Info> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UserDto.Info user = UserDto.Info.builder().id((long)i).bio("bio").name("aaa").photo(new PhotoDto.Photo()).build();
            users.add(user);
        }

        given(userService.getUsers(any(SearchModel.class))).willReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("keyword", "aa")
                        .queryParam("searchKey", "NAME")
                        .queryParam("index", "0")
                        .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.containsString("aa")))
        ;

    }
    @DisplayName("GET /user?keyword,seach_key,index,size 입력 받아 user list 반환")
    @Test
    public void searchUserFailure400Test() throws Exception {
        String url = "/user";

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("keyword", "aa")
                        .queryParam("search_key", "nam")
                        .queryParam("index", "0")
                        .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
        ;

    }

    @Test
    public void userDtoEqualsTest() {
        UserDto userDto = UserDto.builder().id(1L).email("user1@test.test").name("test").build();
        UserDto userDto2 = UserDto.builder().id(1L).email("user1@test.test").name("test").build();

        UserDto.UpdatePassword password = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("newPassword").build();
        UserDto.UpdatePassword password2 = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("newPassword").build();

        Assertions.assertEquals(userDto2, userDto);
        Assertions.assertEquals(password2, password);
    }

    @DisplayName("PATCH /user/{userId}/password, body, jwt 입력받아 UserDto 반환, user 정보 일치하지 않으면 404, 동일한 비밀번호의 경우 409")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void updatePasswordTest() throws Exception {
        String url = "/user/1/password";
        String url404 = "/user/2/password";

        UserDto.UpdatePassword password = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("newPassword").build();
        UserDto.UpdatePassword samePassword = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("oldPassword").build();
        UserDto userDto = UserDto.builder().id(1L).email("user1@test.test").name("test").build();

        given(userService.updatePassword(eq(1L), eq(password), eq(userDto))).willReturn(UserDto.Info.builder().id(1L).build());
        given(userService.updatePassword(eq(2L), eq(password), eq(userDto))).willThrow(new ZooooException(ErrorCode.USER_NOT_FOUND));
        given(userService.updatePassword(eq(1L), eq(samePassword), eq(userDto))).willThrow(new ZooooException(ErrorCode.SAME_PASSWORD));

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(password))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)));

        mockMvc.perform(MockMvcRequestBuilders.patch(url404)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(password))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(samePassword))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @DisplayName("PATCH /user/{userId}/bio, body, jwt 입력 받아 변경된 bio 담긴 user dto 반환. 일치하지 않는 유저의 경우 404")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void updateBioTest() throws Exception {
        String url = "/user/1/bio";
        String url404 = "/user/2/bio";

        Long userId = 1L;

        UserDto userDto = UserDto.builder().id(userId).name("test").email("user1@test.test").build();
        UserDto.Info updateBio = UserDto.Info.builder().id(userId).bio("new bio").build();
        UserDto.Info updateBio404 = UserDto.Info.builder().id(2L).bio("new bio").build();

        given(userService.updateBio(eq(updateBio), eq(userDto))).willReturn(updateBio);
        given(userService.updateBio(eq(updateBio404), eq(userDto))).willThrow(new ZooooException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateBio))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bio", Matchers.is(updateBio.getBio())));

        mockMvc.perform(MockMvcRequestBuilders.patch(url404)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateBio404))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("user 이름으로 정보 받아오기")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    void findByNameTest() throws Exception {
        String url = "/name/test/user";

        UserDto.Info user = UserDto.Info.builder().id(1L).name("test").followerCount(0L).following(true).followingCount(2L).build();

        given(userService.findByName(eq("test"), any(UserDto.class))).willReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(url))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("test")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.following", Matchers.is(true)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.follower_count", Matchers.notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.following_count", Matchers.notNullValue()));
    }
}
