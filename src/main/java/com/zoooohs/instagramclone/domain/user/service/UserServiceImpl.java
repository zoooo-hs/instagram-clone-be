package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto.Info;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDto.Info getInfo(Long userId) {
        UserEntity user = this.userRepository.findById(userId).orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        // TODO: userDto 입력 받아서 generateUserInfo 사용하기
        return this.modelMapper.map(user, UserDto.Info.class);
    }

    @Transactional
    @Override
    public UserDto.Info updateBio(UserDto.Info userDto, UserDto authUserDto) {
        if (!userDto.getId().equals(authUserDto.getId())) {
            throw new ZooooException(ErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findById(authUserDto.getId())
                .map(entity -> {
                    entity.setBio(userDto.getBio());
                    return userRepository.save(entity);
                })
                .map(entity -> modelMapper.map(entity,UserDto.Info.class))
                .orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
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

    @Override
    public UserDto.Info updatePassword(Long userId, UserDto.UpdatePassword passwordDto, UserDto authUserDto) {
        // same password 검증. 보안을 위해 same password 를 먼저 검사
        if (passwordDto.isSamePassword()) {
            throw new ZooooException(ErrorCode.SAME_PASSWORD);
        }
        // user 검증
        if (!userId.equals(authUserDto.getId())) {
            throw new ZooooException(ErrorCode.USER_NOT_FOUND);
        }
        UserEntity user = userRepository.findById(userId)
                .filter(userEntity -> passwordEncoder.matches(passwordDto.getOldPassword(), userEntity.getPassword()))
                .orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        // password update
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        user = userRepository.save(user);
        // map to user info
        return modelMapper.map(user, UserDto.Info.class);
    }

	@Override
	public Info findByName(String name, UserDto userDto) {
        UserEntity userEntity = userRepository.findByName(name)
            .orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        return generateUserInfo(userDto, userEntity);
    }

    private Info generateUserInfo(UserDto userDto, UserEntity userEntity) {
        // TODO: 최적화? 가독성? 어떤게 중요할까?
        // userid, followid 둘 중 하나라도 매칭하는거 찾아서 filter로 걸러내는 방법도 있음
        List<FollowEntity> followings = followRepository.findByUserId(userEntity.getId());
        List<FollowEntity> followers = followRepository.findByFollowUserId(userEntity.getId());
        boolean following = userEntity.getId().equals(userDto.getId()) ||
                followers.stream().anyMatch(f -> f.getUser().getName().equals(userDto.getName()));

        Info info = modelMapper.map(userEntity, Info.class);
        info.setFollowing(following);
        info.setFollowerCount((long) followers.size());
        info.setFollowingCount((long) followings.size());
        return info;
    }
}
