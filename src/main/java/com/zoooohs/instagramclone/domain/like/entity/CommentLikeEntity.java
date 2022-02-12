package com.zoooohs.instagramclone.domain.like.entity;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "comment_like")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "u_comment_user_comment_like", columnNames = {"comment_id", "user_id"}),
})
public class CommentLikeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public CommentLikeEntity(CommentEntity comment, UserEntity user) {
        this.comment = comment;
        this.user = user;
    }
}

