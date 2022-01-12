package com.zoooohs.instagramclone.domain.post.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "post")
public class PostEntity extends BaseEntity {
    // TODO: hash tag 알 수 있는 방법 추가하기
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
