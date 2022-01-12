package com.zoooohs.instagramclone.domain.user.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user")
public class UserEntity extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "bio")
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_photo_id")
    private PhotoEntity profilePhoto;
}
