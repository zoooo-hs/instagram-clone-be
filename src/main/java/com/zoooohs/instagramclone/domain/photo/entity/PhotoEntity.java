package com.zoooohs.instagramclone.domain.photo.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity(name = "photo")
public class PhotoEntity extends BaseEntity {
    @Column(name = "path", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    public void setPost(PostEntity post) {
        this.post = post;
        if (post.getPhotos() == null) {
            post.setPhotos(new ArrayList<>());
        }
        if (!post.getPhotos().contains(this)) {
            post.getPhotos().add(this);
        }
    }
}
