package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto.Info getInfo(Long userId);

    public UserDto.Info updateBio(String bio, UserDto authUserDto);

    List<UserDto.Info> getUsers(SearchModel searchModel);

    UserDto.Info updatePassword(Long userId, UserDto.UpdatePassword passwordDto, UserDto authUserDto);
}
