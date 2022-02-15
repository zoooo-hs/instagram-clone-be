package com.zoooohs.instagramclone.domain.comment.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import lombok.Builder;
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
@NamedEntityGraphs({
        @NamedEntityGraph(name = "comment-user", attributeNodes = {
                @NamedAttributeNode(value = "user"),
                @NamedAttributeNode(value = "likes"),
        }),
})
public class CommentEntity extends BaseEntity {
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private Set<CommentLikeEntity> likes = new HashSet<>();

    public Long getLikeCount() {
        return (long) likes.size();
    }

    @Builder
    public CommentEntity(String content, PostEntity post, UserEntity user) {
        this.content = content;
        this.post = post;
        this.user = user;
    }
}
