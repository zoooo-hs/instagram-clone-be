package com.zoooohs.instagramclone.domain.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    private String content;

    @Builder
    public CommentDto(String content) {
        this.content = content;
    }
}
