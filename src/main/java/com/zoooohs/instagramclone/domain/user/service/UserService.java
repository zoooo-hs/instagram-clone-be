package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;

public interface UserService {
    public UserDto.Info getInfo(Long userId);

    public UserDto.Info updateBio(String bio, UserDto authUserDto);
}
