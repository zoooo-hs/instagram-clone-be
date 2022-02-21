package com.zoooohs.instagramclone.domain.hashtag.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.hashtag.dto.HashTagDto;
import com.zoooohs.instagramclone.domain.hashtag.dto.Search;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.hashtag.repository.HashTagRepository;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HashTagServiceTest {

    private HashTagService hashTagService;
    private String content;

    @Mock
    private HashTagRepository hashTagRepository;

    @Spy
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        hashTagService = new HashTagServiceImpl(hashTagRepository, modelMapper);
        content = "#hello#world 123 #bye_bye_bye, #$#B";
    }

    @DisplayName(" 게시글 내용기반 해쉬태그를 뽑아내기 →   List<string>")
    @Test
    public void extractHashTagsTest() {
        List<String> expected = List.of("#hello", "#world", "#bye_bye_bye","#B");

        List<String> actuals = hashTagService.extract(content);

        assertNotNull(actuals);
        assertEquals(expected.size(), actuals.size());
        for (String actual: actuals) {
           assertTrue(expected.contains(actual));
        }
    }

    @DisplayName("게시글 내용, 포스트 id 받아서 해쉬 태그 추가 제거하는  서비스 -> 새로 저장된 해쉬 태그 반환")
    @Test
    public void manageHashTagTest() {
        /**
         * post (1L) -> #I #like #hello  3가지 해쉬 태그 있음
         *
         * content -> "#hello#world 123 #bye_bye_bye, #$ #B" 들어오면 기존의 I, like는 사라지고 새로운 content의 해쉬태그만 남음
         *
         * return -> List<HashTagDto> -> "#hello#world#bye_bye_bye#B
         */
        Long postId = 1L;

        PostEntity post = PostEntity.builder().id(postId).build();

        List<HashTagEntity> hashTagEntities = List.of(
                HashTagEntity.builder().post(post).tag("#I").build(),
                HashTagEntity.builder().post(post).tag("#like").build(),
                HashTagEntity.builder().post(post).tag("#hello").build()
        );

        given(hashTagRepository.findByPostId(eq(postId))).willReturn(hashTagEntities);

        List<HashTagDto> actual = hashTagService.manage(content, postId);

        assertEquals(4, actual.size());
        assertEquals(0, actual.stream().map(HashTagDto::getTag).filter(List.of("#I", "#like")::contains).count());
    }

    @DisplayName("게시글 내용, null post id 받아서 해쉬 태그 추가 제거하는  서비스 -> 새로 저장된 해쉬 태그 반환")
    @Test
    public void manageHashTagWithNullPostIdTest() {
        /**
         * post null
         *
         * content -> "#hello#world 123 #bye_bye_bye, #$ #B" 들어오면 기존의 I, like는 사라지고 새로운 content의 해쉬태그만 남음
         *
         * return -> List<HashTagDto> -> "#hello#world#bye_bye_bye#B
         */

        List<HashTagDto> actual = hashTagService.manage(content, null);

        assertEquals(4, actual.size());
    }

    @DisplayName("paging, keyword 받아와 hashtag 리스트 반환하는 서비스")
    @Test
    public void searchTest() {
        SearchModel searchModel = new SearchModel();
        searchModel.setKeyword("hello");
        searchModel.setIndex(0);
        searchModel.setSize(20);

        List<Search> hashTagDtos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Search hashTagDto = Search.builder().tag("hello"+i).count((long)20-i).build();
            hashTagDtos.add(hashTagDto);
        }

        given(hashTagRepository.searchLikeTag(any(String.class), any(Pageable.class))).willReturn(hashTagDtos);

        List<Search> actual = hashTagService.search(searchModel);

        assertInstanceOf(Long.class, actual.get(0).getCount());
        assertEquals(hashTagDtos.size(), actual.size());
    }

}
