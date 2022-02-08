package com.zoooohs.instagramclone.domain.comment.dto;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
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

    private UserDto.Feed user;

    @Builder
    public CommentDto(String content, UserDto.Feed user) {
        this.content = content;
        this.user = user;
    }
}