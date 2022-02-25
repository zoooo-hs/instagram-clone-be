package com.zoooohs.instagramclone.domain.comment.entity;

import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "comment_comment")
@DiscriminatorValue("comment_comment")
public class CommentCommentEntity extends CommentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Builder
    public CommentCommentEntity(String content, UserEntity user, CommentEntity comment) {
        super(content, user);
        this.comment = comment;
    }
}
