package com.zoooohs.instagramclone.domain.post.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PostDto {
    @Data
    public static class Post {
        private Long id;
        @NotNull
        private String description;
        private List<PhotoDto.Photo> photos;
        private UserDto.Feed user;
    }
}
