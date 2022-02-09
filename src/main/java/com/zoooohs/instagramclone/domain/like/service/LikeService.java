package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.LikeDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

public interface LikeService {

    LikeDto like(Long postId, UserDto userDto);
}
