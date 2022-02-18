package com.zoooohs.instagramclone.domain.hashtag.repository;

import com.zoooohs.instagramclone.domain.hashtag.dto.Search;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {
    List<HashTagEntity> findByPostId(Long postId);

    @Query(value = "SELECT new com.zoooohs.instagramclone.domain.hashtag.dto.Search(ht.tag, COUNT(ht.tag)) FROM hash_tag ht WHERE ht.tag LIKE %:tag% GROUP BY ht.tag ORDER BY COUNT(ht.tag) DESC")
    List<Search> searchLikeTag(@Param("tag") String tag, Pageable pageable);
}
