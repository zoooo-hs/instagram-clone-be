package com.zoooohs.instagramclone.domain.user.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;

    @Data
    public static class Feed {
        private Long id;
        private String name;
    }

    @Data
    public static class Info {
        private Long id;
        private String name;
        private String bio;
        private PhotoDto.Photo profilePhoto;
    }

    public String getUsername() {
        return email;
    }
}
