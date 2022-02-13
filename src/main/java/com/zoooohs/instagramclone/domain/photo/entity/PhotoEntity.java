package com.zoooohs.instagramclone.domain.photo.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity(name = "photo")
public class PhotoEntity extends BaseEntity {
    @Column(name = "path", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public PhotoEntity(String path, PostEntity post) {
        this.path = path;
        this.post = post;
    }
}
