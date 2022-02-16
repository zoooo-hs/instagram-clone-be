package com.zoooohs.instagramclone.domain.user.controller;

import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

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
}
