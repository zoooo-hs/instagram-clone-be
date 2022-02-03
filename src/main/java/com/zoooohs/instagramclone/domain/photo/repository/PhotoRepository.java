package com.zoooohs.instagramclone.domain.photo.repository;

import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {
}
