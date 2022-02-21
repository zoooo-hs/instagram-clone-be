package com.zoooohs.instagramclone.domain.hashtag.repository;

import com.zoooohs.instagramclone.domain.hashtag.dto.Search;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HashTagRepositoryTest {

    private PasswordEncoder passwordEncoder;
    private UserEntity user;
    private PostEntity post;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private HashTagRepository hashTagRepository;
    private List<String> tags;
    private Set<HashTagEntity> hashTagEntities;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Date now = new Date();
        String testEmail = "tt-sign-up-test-id"+now.getTime()+"@email.com";
        String testPassword = "passwd";
        String testName = "sign-up-test-name"+now.getTime();
        String content = "#hello#world 123 #bye_bye_bye, #$";
        user = new UserEntity();
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode(testPassword));
        user.setName(testName);
        user = this.userRepository.save(user);
        post = PostEntity.builder().description(content).user(user).build();
        post = this.postRepository.save(post);

        tags = List.of("#hello", "#world", "#bye_bye_bye");
        hashTagEntities = tags.stream().map(tag -> HashTagEntity.builder().post(post).tag(tag).build()).collect(Collectors.toSet());
    }

    @DisplayName("해쉬 태그, 게시글 쌍 entity save  테스트")
    @Test
    public void saveAllTest() {
        List<HashTagEntity> actual = hashTagRepository.saveAll(hashTagEntities);

        assertEquals(tags.size(), actual.size());
        assertEquals(tags.size(), actual.stream().map(HashTagEntity::getTag).filter(tags::contains).count());
    }

    @DisplayName("post로 hash tag 저장 테스트")
    @Test
    public void saveFromPostMergeTest() {
        post.setHashTags(hashTagEntities);

        postRepository.save(post).getHashTags();

        List<HashTagEntity> actual = hashTagRepository.findByPostId(post.getId());

        assertEquals(tags.size(), actual.size());
        assertEquals(tags.size(), actual.stream().map(HashTagEntity::getTag).filter(tags::contains).count());
    }

    @DisplayName("post로 hash tag 저장 테스트")
    @Test
    public void saveFromPostPersistTest() {
        String content = "#hello#world 123 #bye_bye_bye, #$";
        PostEntity post = PostEntity.builder().description(content).user(user).build();
        hashTagEntities = tags.stream().map(tag -> HashTagEntity.builder().tag(tag).build()).collect(Collectors.toSet());
        post.setHashTags(hashTagEntities);

        postRepository.save(post).getHashTags();

        List<HashTagEntity> actual = hashTagRepository.findByPostId(post.getId());

        assertEquals(tags.size(), actual.size());
        assertEquals(tags.size(), actual.stream().map(HashTagEntity::getTag).filter(tags::contains).count());
    }

    @DisplayName("postId 로  hashTagEntity 찾기")
    @Test
    public void findByPostIdTest() {
        hashTagRepository.saveAll(hashTagEntities);

        List<HashTagEntity> actual = hashTagRepository.findByPostId(post.getId());
        assertEquals(tags.size(), actual.size());
        assertEquals(tags.size(), actual.stream().map(HashTagEntity::getTag).filter(tags::contains).count());
    }

    @DisplayName("게시글 지우면 해쉬태그도 같이 제거")
    @Test
    public void postOrphanRemovalTest() {
        hashTagRepository.saveAll(hashTagEntities);

        List<Long> hashTagIds = hashTagEntities.stream().map(HashTagEntity::getId).collect(Collectors.toList());
        Long postId = post.getId();

        entityManager.flush();
        entityManager.clear();
        entityManager.detach(post);
        for (HashTagEntity hashTagEntity: hashTagEntities) {
            entityManager.detach(hashTagEntity);
        }

        post = postRepository.findByIdForDelete(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);

        List<HashTagEntity> actual = hashTagRepository.findByPostId(postId);
        assertEquals(0, actual.size());

        actual = hashTagRepository.findAllById(hashTagIds);
        assertEquals(0, actual.size());
    }

    @DisplayName("keyword, pageable  받아와, hashtag 리스트 반환하는 레포지토리")
    @Test
    public void searchLikeTagTest() {
        hashTagRepository.saveAll(hashTagEntities);

        List<Search> actual = hashTagRepository.searchLikeTag("e", PageRequest.of(0, 20));

        assertEquals(2, actual.size());
    }
}
