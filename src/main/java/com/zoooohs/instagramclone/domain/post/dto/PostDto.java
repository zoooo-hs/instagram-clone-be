package com.zoooohs.instagramclone.domain.post.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    @Schema(name = "PostDto.Post")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private Long id;

        @NotNull
        @Schema(description = "게시글 설명 (글 내용)")
        private String description;

        @Schema(description = "게시글 사진 리스트")
        private List<PhotoDto.Photo> photos;

        @Schema(description = "게시글 작성자")
        private UserDto.Feed user;

        @Schema(description = "게시글 좋아요 개수")
        private Long likeCount;

        @Accessors(fluent = true)
        @Schema(name = "liked", description = "내가 좋아요를 눌렀는지 여부")
        private Boolean isLiked;

        @Schema(name = "likedId", description = "내가 누른 좋아요 id")
        private Long likedId;

        @Schema(description = "게시글 댓글 개수")
        private Long commentCount;

        private LocalDateTime createdAt;
    }
}
