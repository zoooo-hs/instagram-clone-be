package com.zoooohs.instagramclone.domain.post.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PostDto {
    @Data
    @NoArgsConstructor
    public static class Post {
        private Long id;
        @NotNull
        private String description;
        private List<PhotoDto.Photo> photos;
        private UserDto.Feed user;
        private Long likeCount;
        @Accessors(fluent = true)
        private Boolean isLiked;

        @Builder
        public Post(Long id, String description, List<PhotoDto.Photo> photos, UserDto.Feed user, Long likeCount, Boolean isLiked) {
            this.id = id;
            this.description = description;
            this.photos = photos;
            this.user = user;
            this.likeCount = likeCount;
            this.isLiked = isLiked;
        }
    }
}
