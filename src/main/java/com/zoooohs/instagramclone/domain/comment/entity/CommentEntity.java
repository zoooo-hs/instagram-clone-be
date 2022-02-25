package com.zoooohs.instagramclone.domain.comment.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "comment")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "commentType")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "comment-user", attributeNodes = {
                @NamedAttributeNode(value = "user"),
                @NamedAttributeNode(value = "likes"),
        }),
})
public abstract class CommentEntity extends BaseEntity {
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<CommentLikeEntity> likes = new HashSet<>();

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<CommentCommentEntity> comments = new HashSet<>();

    public Long getLikeCount() {
        return (long) likes.size();
    }

    public CommentEntity(String content, UserEntity user) {
        this.content = content;
        this.user = user;
    }
}
