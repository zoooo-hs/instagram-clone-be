package com.zoooohs.instagramclone.domain.follow.service;

import java.util.List;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto.Info;

public interface FollowService {
    FollowDto follow(Long followUserId, Long userId);

    Long unfollow(Long followUserId, Long userId);

    List<UserDto.Info> findByUserId(Long userId, UserDto userDto);

    List<Info> findByFollowUserId(Long userId, UserDto userDto);
}
