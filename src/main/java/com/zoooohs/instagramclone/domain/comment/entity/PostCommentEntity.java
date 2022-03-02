package com.zoooohs.instagramclone.domain.comment.entity;

import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "post_comment")
@DiscriminatorValue("post_comment")
public class PostCommentEntity extends CommentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Builder
    public PostCommentEntity(String content, PostEntity post, UserEntity user) {
        super(content, user);
        this.post = post;
    }
}
