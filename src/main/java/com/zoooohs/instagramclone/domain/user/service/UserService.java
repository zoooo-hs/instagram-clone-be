package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto.Info getInfo(Long userId);

    UserDto.Info updateBio(UserDto.Info userDto, UserDto authUserDto);

    List<UserDto.Info> getUsers(SearchModel searchModel);

    UserDto.Info updatePassword(Long userId, UserDto.UpdatePassword passwordDto, UserDto authUserDto);
}
