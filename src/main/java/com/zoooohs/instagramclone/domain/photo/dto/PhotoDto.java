package com.zoooohs.instagramclone.domain.photo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class PhotoDto {
    @Data
    @NoArgsConstructor
    public static class Photo {
        private Long id;
        @NotNull
        private String path;

        @Builder
        public Photo(Long id, String path) {
            this.id = id;
            this.path = path;
        }
    }
}
