package com.zoooohs.instagramclone.domain.photo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

public class PhotoDto {
    @Data
    public static class Photo {
        private Long id;
        @NotNull
        private String path;
    }
}
