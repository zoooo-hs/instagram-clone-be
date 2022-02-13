package com.zoooohs.instagramclone.domain.follow.service;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;

public interface FollowService {
    FollowDto follow(Long followUserId, Long userId);
}
