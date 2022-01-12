package com.zoooohs.instagramclone.domain.photo.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity(name = "photo")
public class PhotoEntity extends BaseEntity {
    @Column(name = "path", nullable = false)
    private String path;
}
