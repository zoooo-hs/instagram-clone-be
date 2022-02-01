package com.zoooohs.instagramclone.domain.user.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;

    @Builder
    public UserDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

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
