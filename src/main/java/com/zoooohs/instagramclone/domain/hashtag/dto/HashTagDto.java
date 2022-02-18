package com.zoooohs.instagramclone.domain.hashtag.dto;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashTagDto {
    private Long id;
    private PostDto.Post post;
    private String tag;

    @Builder
    public HashTagDto(PostDto.Post post, String tag) {
        this.post = post;
        this.tag = tag;
    }
}
