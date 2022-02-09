package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.PostLikeRepository;
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
    private final PostLikeRepository postLikeRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostLikeDto likePost(Long postId, UserDto userDto) {
        PostEntity post = postRepository.findByIdAndUserId(postId, userDto.getId());
        if (post == null) {
            throw new ZooooException(ErrorCode.POST_NOT_FOUND);
        }
        PostLikeEntity like = PostLikeEntity.builder().post(post).user(UserEntity.builder().id(userDto.getId()).build()).build();
        like = postLikeRepository.save(like);
        return modelMapper.map(like, PostLikeDto.class);
    }

    @Override
    public Long unlikePost(Long postId, UserDto userDto) {
        PostLikeEntity like = postLikeRepository.findByPostIdAndUserId(postId, userDto.getId());
        if (like == null) {
            throw new ZooooException(ErrorCode.LIKE_NOT_FOUND);
        }
        long likeId = like.getId();
        postLikeRepository.delete(like);
        return likeId;
    }
}
