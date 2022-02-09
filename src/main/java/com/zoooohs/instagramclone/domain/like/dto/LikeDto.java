package com.zoooohs.instagramclone.domain.like.dto;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private PostDto.Post post;

    @Builder
    public LikeDto(Long id, PostDto.Post post) {
        this.id = id;
        this.post = post;
    }
}
