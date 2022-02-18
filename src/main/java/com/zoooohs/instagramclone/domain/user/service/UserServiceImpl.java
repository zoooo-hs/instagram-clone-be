package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<UserDto.Info> getUsers(SearchModel searchModel) {
        List<UserEntity> users = null;
        if (searchModel.getSearchKey().equals(SearchKeyType.NAME)) {
            Pageable pageable = PageRequest.of(searchModel.getIndex(), searchModel.getSize());
            users = userRepository.findByNameIgnoreCaseContaining(searchModel.getKeyword(), pageable);
        }
        return Optional.ofNullable(users).map(Collection::stream)
                .map(stream -> stream.map(entity -> modelMapper.map(entity, UserDto.Info.class)).collect(Collectors.toList())).orElse(List.of());
    }
}
