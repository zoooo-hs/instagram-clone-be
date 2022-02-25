package com.zoooohs.instagramclone.domain.like.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "likes")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "LikeType")
public abstract class LikeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    protected UserEntity user;

    public LikeEntity(UserEntity user) {
        this.user = user;
    }
}
