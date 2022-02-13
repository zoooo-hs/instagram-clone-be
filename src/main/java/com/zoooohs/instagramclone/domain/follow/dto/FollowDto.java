package com.zoooohs.instagramclone.domain.follow.dto;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowDto {
    private Long id;
    private UserDto.Info followUser;

    @Builder
    public FollowDto(Long id, UserDto.Info follow) {
        this.id = id;
        this.followUser = follow;
    }
}
