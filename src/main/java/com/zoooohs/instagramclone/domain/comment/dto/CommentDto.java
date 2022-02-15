package com.zoooohs.instagramclone.domain.comment.dto;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    private String content;

    private UserDto.Feed user;

    private Long likeCount;
    @Accessors(fluent = true)
    private Boolean isLiked;


    @Builder
    public CommentDto(Long id, String content, UserDto.Feed user, Long likeCount, Boolean isLiked) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
