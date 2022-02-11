package com.zoooohs.instagramclone.domain.like.dto;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentLikeDto {
    private Long id;
    private CommentDto comment;

    @Builder
    public CommentLikeDto(Long id, CommentDto comment) {
        this.id = id;
        this.comment = comment;
    }
}
