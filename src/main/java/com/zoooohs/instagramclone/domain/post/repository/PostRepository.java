package com.zoooohs.instagramclone.domain.post.repository;

import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @EntityGraph("post-feed")
    @Query("SELECT p FROM PostEntity p WHERE p.user.id != :userId ORDER BY p.id DESC")
    List<PostEntity> findAllExceptUserId(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph("post-feed")
    List<PostEntity> findByUserId(Long userId);
}
