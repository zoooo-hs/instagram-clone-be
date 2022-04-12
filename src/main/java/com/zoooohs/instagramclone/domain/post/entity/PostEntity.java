package com.zoooohs.instagramclone.domain.post.entity;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
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
@Entity
@Table(name = "post")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "post-feed", attributeNodes = {
                @NamedAttributeNode(value = "user"),
                @NamedAttributeNode(value = "photos"),
                @NamedAttributeNode(value = "likes"),
        }),
        @NamedEntityGraph(name = "post-all-child", attributeNodes = {
                @NamedAttributeNode(value = "hashTags"),
        }),
})
public class PostEntity extends BaseEntity {
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // TODO: cascade 범위 다시 고려
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PhotoEntity> photos = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<PostLikeEntity> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<HashTagEntity> hashTags = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<PostCommentEntity> comments = new HashSet<>();

    public Long getLikeCount() {
        return (long) likes.size();
    }
    public Long getCommentCount() {
        return (long) comments.size();
    }

    public void setHashTags(Set<HashTagEntity> hashTags) {
        this.hashTags.clear();
        if (hashTags != null) {
            this.hashTags.addAll(hashTags);
        }
//        if (hashTags == null) {
//            return;
//        }
        for (HashTagEntity hashTag: hashTags) {
            if (hashTag.getPost() == null) {
                hashTag.setPost(this);
            }
        }
    }

    public void setPhotos(Set<PhotoEntity> photos) {
        this.photos = photos;
        if (photos == null) {
            return;
        }
        for (PhotoEntity photo: photos) {
            if (photo.getPost() == null) {
                photo.setPost(this);
            }
        }
    }

    @Builder
    public PostEntity(Long id, String description, UserEntity user) {
        this.id = id;
        this.description = description;
        this.user = user;
        this.photos = new HashSet<>();
    }
}
