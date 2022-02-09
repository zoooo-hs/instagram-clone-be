package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.LikeDto;
import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.LikeRepository;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ModelMapper modelMapper;

    @Override
    public LikeDto like(Long postId, UserDto userDto) {
        PostEntity post = postRepository.findByIdAndUserId(postId, userDto.getId());
        if (post == null) {
            throw new ZooooException(ErrorCode.POST_NOT_FOUND);
        }
        LikeEntity like = LikeEntity.builder().post(post).user(UserEntity.builder().id(userDto.getId()).build()).build();
        like = likeRepository.save(like);
        return modelMapper.map(like, LikeDto.class);
    }
}
