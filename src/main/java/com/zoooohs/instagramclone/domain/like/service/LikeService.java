package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

public interface LikeService {

    PostLikeDto likePost(Long postId, UserDto userDto);

    Long unlikePost(Long postId, UserDto userDto);
}
