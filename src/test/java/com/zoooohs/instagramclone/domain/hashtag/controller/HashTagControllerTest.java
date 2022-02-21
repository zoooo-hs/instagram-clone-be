package com.zoooohs.instagramclone.domain.hashtag.controller;

import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.hashtag.dto.Search;
import com.zoooohs.instagramclone.domain.hashtag.service.HashTagService;
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
        controllers = HashTagController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class HashTagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    HashTagService hashTagService;

    @DisplayName("GET /hash-tag, paging, keyword 입력받아 해쉬 태그 리스트 반환")
    @Test
    public void searchTest() throws Exception {
        String url = "/hash-tag";

        List<Search> hashTagDtos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Search hashTagDto = Search.builder().tag("hello"+i).count((long)20-i).build();
            hashTagDtos.add(hashTagDto);
        }

        given(hashTagService.search(any(SearchModel.class))).willReturn(hashTagDtos);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("keyword", "#hello")
                        .queryParam("index", "0")
                        .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].count", Matchers.instanceOf(Integer.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", Matchers.instanceOf(String.class)))
        ;
    }
}
