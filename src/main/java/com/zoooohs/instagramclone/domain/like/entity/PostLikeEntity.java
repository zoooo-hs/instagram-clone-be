package com.zoooohs.instagramclone.domain.like.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "post_like")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "u_post_user_post_like", columnNames = {"post_id", "user_id"}),
})
public class PostLikeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public PostLikeEntity(PostEntity post, UserEntity user) {
        this.post = post;
        this.user = user;
    }
}
