package com.zoooohs.instagramclone.domain.like.entity;

import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "post_like")
@DiscriminatorValue("post_like")
public class PostLikeEntity extends LikeEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Builder
    public PostLikeEntity(PostEntity post, UserEntity user) {
        super(user);
        this.post = post;
    }
}
