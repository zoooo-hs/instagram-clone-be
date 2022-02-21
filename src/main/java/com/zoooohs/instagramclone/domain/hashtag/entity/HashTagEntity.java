package com.zoooohs.instagramclone.domain.hashtag.entity;

import com.zoooohs.instagramclone.domain.common.entity.BaseEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "hash_tag")
@NoArgsConstructor
public class HashTagEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(name = "tag")
    private String tag;

    @Builder
    public HashTagEntity(PostEntity post, String tag) {
        this.post = post;
        this.tag = tag;
    }
}
