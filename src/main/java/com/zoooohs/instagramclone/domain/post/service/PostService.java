package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

public interface PostService {
    public PostDto.Post create(PostDto.Post postDto, UserDto userDto);
}
