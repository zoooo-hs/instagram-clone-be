package com.zoooohs.instagramclone.domain.post.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
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
})
public class PostEntity extends BaseEntity {
    // TODO: hash tag 알 수 있는 방법 추가하기
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PhotoEntity> photos = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostLikeEntity> likes = new HashSet<>();

    public Long getLikeCount() {
        return (long) likes.size();
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
