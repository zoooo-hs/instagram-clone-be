package com.zoooohs.instagramclone.domain.like.entity;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "comment_like")
@DiscriminatorValue("comment_like")
public class CommentLikeEntity extends LikeEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Builder
    public CommentLikeEntity(CommentEntity comment, UserEntity user) {
        super(user);
        this.comment = comment;
    }
}

