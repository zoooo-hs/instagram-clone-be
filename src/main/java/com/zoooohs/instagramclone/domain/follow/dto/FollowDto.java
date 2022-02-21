package com.zoooohs.instagramclone.domain.follow.dto;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowDto {
    private Long id;

    @Schema(description = "팔로우한 유저")
    private UserDto.Info followUser;

    @Builder
    public FollowDto(Long id, UserDto.Info follow) {
        this.id = id;
        this.followUser = follow;
    }
}
