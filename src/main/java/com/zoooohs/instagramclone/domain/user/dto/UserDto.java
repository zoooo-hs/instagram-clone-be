package com.zoooohs.instagramclone.domain.user.dto;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
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

    @Schema(name = "UserDto.Feed", description = "게시글 피드 리스트에 유저 정보를 나타내기 위한 DTO")
    @Data
    @NoArgsConstructor
    public static class Feed {
        private Long id;
        private String name;

        @Builder
        public Feed(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Schema(name = "UserDto.Info", description = "유저 세부 정보를 나타내기 위한 DTO")
    @Data
    @NoArgsConstructor
    public static class Info {
        private Long id;
        private String name;

        @Schema(description = "바이오")
        private String bio;

        @Schema(description = "프로필 사진")
        private PhotoDto.Photo photo;

        @Builder
        public Info(Long id, String name, String bio, PhotoDto.Photo photo) {
            this.id = id;
            this.name = name;
            this.bio = bio;
            this.photo = photo;
        }
    }

    public String getUsername() {
        return email;
    }
}
