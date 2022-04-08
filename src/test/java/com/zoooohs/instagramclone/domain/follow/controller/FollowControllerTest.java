package com.zoooohs.instagramclone.domain.follow.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.service.FollowService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = FollowController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class FollowControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FollowService followService;
    private Long followUserId;
    private Long userNotFoundId;
    private String url;
    private String url404;
    private String url409Self;

    @BeforeEach
    public void setUp() {
        followUserId = 2L;
        userNotFoundId = 3L;
        url = String.format("/user/%d/follow", followUserId);
        url404 = String.format("/user/%d/follow", userNotFoundId);
        url409Self = String.format("/user/%d/follow", 1L);
    }

    @DisplayName("POST /user/{userId}/follow, jwt를 입력받아 follow json 반환, userId 없으면 404, 이미 follow 한 user면 409 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void followTest() throws Exception {
        String url409 = String.format("/user/%d/follow", followUserId);

        given(followService.follow(eq(followUserId), eq(1L))).willReturn(FollowDto.builder().id(1L).follow(UserDto.Info.builder().id(followUserId).build()).build());
        given(followService.follow(eq(userNotFoundId), eq(1L))).willThrow(new ZooooException(ErrorCode.USER_NOT_FOUND));
        given(followService.follow(eq(1L), eq(1L))).willThrow(new ZooooException(ErrorCode.FOLLOWING_SELF));

        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.instanceOf(Integer.class)))
                .andExpect(jsonPath("$.follow_user.id", Matchers.instanceOf(Integer.class)));

        mockMvc.perform(post(url404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", Matchers.is(ErrorCode.USER_NOT_FOUND.name())));


        mockMvc.perform(post(url409Self))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", Matchers.is(ErrorCode.FOLLOWING_SELF.name())));

        given(followService.follow(eq(followUserId), eq(1L))).willThrow(new ZooooException(ErrorCode.ALREADY_FOLLOWED_USER));

        mockMvc.perform(post(url409))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", Matchers.is(ErrorCode.ALREADY_FOLLOWED_USER.name())));
    }

    @DisplayName("DELETE /user/{followUserId}/follow, jwt 입력 id 반환. follow가 없을 경우 404, 자기 자신을 언팔로우 할 경우 409")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void unfollowTest() throws Exception {

        given(followService.unfollow(eq(followUserId), eq(1L))).willReturn(1L);
        given(followService.unfollow(eq(userNotFoundId), eq(1L))).willThrow(new ZooooException(ErrorCode.FOLLOW_NOT_FOUND));
        given(followService.unfollow(eq(1L), eq(1L))).willThrow(new ZooooException(ErrorCode.FOLLOWING_SELF));

        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(delete(url404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", Matchers.is(ErrorCode.FOLLOW_NOT_FOUND.name())));

        mockMvc.perform(delete(url409Self))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", Matchers.is(ErrorCode.FOLLOWING_SELF.name())));
    }

    @DisplayName("GET /user/{userId}/follow/follow-user 로 userId가 팔로우하는 모든 유저 리스트 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    void findByUserIdTest() throws Exception {
        String url = "/user/1/follow/follow-user";

        UserDto.Info following = UserDto.Info.builder().id(2L).build();

        given(followService.findByUserId(eq(1L), any(UserDto.class))).willReturn(List.of(following));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(2)))
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @DisplayName("GET /follow-user/{userId}/follow/user 로 userId를 팔로우하는 모든 유저 리스트 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    void findByFollowUserIdTest() throws Exception {
        String url = "/follow-user/1/follow/user";

        UserDto.Info following = UserDto.Info.builder().id(2L).build();

        given(followService.findByFollowUserId(eq(1L), any(UserDto.class))).willReturn(List.of(following));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(2)))
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }
}
