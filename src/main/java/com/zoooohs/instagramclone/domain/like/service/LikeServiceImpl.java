package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.like.dto.CommentLikeDto;
import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
import com.zoooohs.instagramclone.domain.like.repository.LikeRepository;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public PostLikeDto likePost(Long postId, UserDto userDto) {
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        PostLikeEntity like = postLikeRepository.findByPostIdAndUserId(postId, userDto.getId());
        if (like != null) {
            throw new ZooooException(ErrorCode.ALREADY_LIKED_POST);
        }
        like = PostLikeEntity.builder().post(post).user(UserEntity.builder().id(userDto.getId()).build()).build();
        like = postLikeRepository.save(like);
        return modelMapper.map(like, PostLikeDto.class);
    }

    @Transactional
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

    @Transactional
    @Override
    public CommentLikeDto likeComment(Long commentId, UserDto userDto) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new ZooooException(ErrorCode.COMMENT_NOT_FOUND));
        CommentLikeEntity commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userDto.getId());
        if (commentLike != null) {
            throw new ZooooException(ErrorCode.ALREADY_LIKED_COMMENT);
        }
        commentLike = CommentLikeEntity.builder().comment(comment).user(UserEntity.builder().id(userDto.getId()).build()).build();
        commentLike = commentLikeRepository.save(commentLike);
        return modelMapper.map(commentLike, CommentLikeDto.class);
    }

    @Transactional
    @Override
    public Long unlikeComment(Long commentId, UserDto userDto) {
        CommentLikeEntity commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userDto.getId());
        if (commentLike == null) {
            throw new ZooooException(ErrorCode.LIKE_NOT_FOUND);
        }
        Long likeId = commentLike.getId();
        commentLikeRepository.delete(commentLike);
        return likeId;
    }

    @Transactional
    @Override
    public Long unlike(Long likeId, UserDto userDto) {
        LikeEntity like = likeRepository.findByIdAndUserId(likeId, userDto.getId()).orElseThrow(() -> new ZooooException(ErrorCode.LIKE_NOT_FOUND));
        likeRepository.delete(like);
        return likeId;
    }
}
