package com.zoooohs.instagramclone.domain.follow.service;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public FollowDto follow(Long followUserId, Long userId) {
        if (followUserId.equals(userId)) {
            throw new ZooooException(ErrorCode.FOLLOWING_SELF);
        }

        UserEntity followUser = userRepository.findById(followUserId).orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        UserEntity user = UserEntity.builder().id(userId).build();

        FollowEntity follow = followRepository.findByFollowUserIdAndUserId(followUserId, userId);
        if (follow != null) {
            throw new ZooooException(ErrorCode.ALREADY_FOLLOWED_USER);
        }
        follow = FollowEntity.builder().followUser(followUser).user(user).build();
        follow = followRepository.save(follow);
        return modelMapper.map(follow, FollowDto.class);
    }
}
