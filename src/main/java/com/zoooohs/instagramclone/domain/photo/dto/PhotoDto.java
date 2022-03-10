package com.zoooohs.instagramclone.domain.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class PhotoDto {

    @Schema(name = "PhotoDto.Photo")
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Photo {
        private Long id;

        @Schema(description = "사진을 사용할 수 있는 경로")
        @NotNull
        private String path;

        @Builder
        public Photo(Long id, String path) {
            this.id = id;
            this.path = path;
        }
    }
}
