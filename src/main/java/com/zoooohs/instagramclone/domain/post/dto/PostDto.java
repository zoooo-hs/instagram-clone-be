package com.zoooohs.instagramclone.domain.post.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        @Builder
        public Post(Long id, String description, UserDto.Feed user) {
            this.id = id;
            this.description = description;
            this.user = user;
            this.photos = new ArrayList<>();
        }
    }
}
