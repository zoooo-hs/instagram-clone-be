package com.zoooohs.instagramclone.domain.comment.dto;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @Schema(description = "댓글 내용")
    @NotNull
    private String content;

    @Schema(description = "댓글 작성자")
    private UserDto.Feed user;

    @Schema(description = "받은 좋아요 개수")
    private Long likeCount;

    @Accessors(fluent = true)
    @Schema(name = "liked", description = "내가 좋아요를 눌렀는지 여부")
    private Boolean isLiked;

    @Schema(description = "대댓글 개수")
    private Long commentCount;

    @Builder
    public CommentDto(Long id, String content, UserDto.Feed user, Long likeCount, Boolean isLiked, Long commentCount) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.commentCount = commentCount;
    }
}
