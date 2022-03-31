package com.zoooohs.instagramclone.domain.post.repository;

import com.zoooohs.instagramclone.domain.post.entity.PostEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.id != :userId ORDER BY p.id DESC")
    List<PostEntity> findAllExceptUserId(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.id = :userId ORDER BY p.id DESC")
    List<PostEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph("post-feed")
    PostEntity findByIdAndUserId(Long postId, Long userId);

    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p ORDER BY p.id DESC")
    List<PostEntity> findAllWithPage(Pageable pageable);

    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.id = :userId ORDER BY p.id DESC")
    List<PostEntity> findMinesAndFollowUsers(Long userId, Pageable pageable);

    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.id IN :userIds ORDER BY p.id DESC")
    List<PostEntity> findAllByUserId(@Param("userIds") List<Long> userIds, Pageable pageable);

    @EntityGraph("post-all-child")
    @Query("SELECT p FROM PostEntity p WHERE p.id = :id")
    Optional<PostEntity> findByIdForDelete(@Param("id") Long postId);

    @EntityGraph("post-feed")
    @Query("SELECT ht.post FROM hash_tag ht JOIN PostEntity p ON ht.post.id = p.id WHERE ht.tag = :tag ORDER BY p.id DESC")
    List<PostEntity> findAllByTag(@Param("tag") String tag, Pageable pageable);

    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.name = :userName ORDER BY p.id DESC")
    List<PostEntity> findByUserName(@Param("userName") String userName, Pageable of);
}
