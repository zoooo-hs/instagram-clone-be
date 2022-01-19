package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto.Info getInfo(Long userId) {
        UserEntity user = this.userRepository.findById(userId).orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        return this.modelMapper.map(user, UserDto.Info.class);
    }

    @Transactional
    @Override
    public UserDto.Info updateBio(String bio, UserDto authUserDto) {
        UserEntity user = this.userRepository.findById(authUserDto.getId()).orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        user.setBio(bio);
        user = this.userRepository.save(user);
        return this.modelMapper.map(user, UserDto.Info.class);
    }
}
