package com.zoooohs.instagramclone.domain.user.dto;

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

    public String getUsername() {
        return email;
    }
}
