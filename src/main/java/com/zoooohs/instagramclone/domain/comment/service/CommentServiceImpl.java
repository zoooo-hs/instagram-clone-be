package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public CommentDto create(CommentDto commentDto, Long postId, UserDto userDto) {
        UserEntity user = UserEntity.builder().id(userDto.getId()).build();
        PostEntity post = this.postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        CommentEntity comment = this.modelMapper.map(commentDto, CommentEntity.class);
        comment.setPost(post);
        comment.setUser(user);
        comment = this.commentRepository.save(comment);
        return this.modelMapper.map(comment, CommentDto.class);
    }

    @Override
    public List<CommentDto> getPostCommentList(Long postId, PageModel pageModel, Long userId) {
        PostEntity post = this.postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        List<CommentEntity> comments = this.commentRepository.findByPostId(post.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
        return comments.stream().map(entity -> {
            CommentDto dto = this.modelMapper.map(entity, CommentDto.class);
            boolean isLiked = entity.getLikes().stream().filter(like -> like.getUser().getId().equals(userId)).findFirst().isPresent();
            dto.isLiked(isLiked);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto, UserDto userDto) {
        CommentEntity comment = this.commentRepository.findByIdAndUserId(commentId, userDto.getId());
        if (comment == null) {
            throw new ZooooException(ErrorCode.COMMENT_NOT_FOUND);
        }
        comment.setContent(commentDto.getContent());
        comment = this.commentRepository.save(comment);
        return this.modelMapper.map(comment, CommentDto.class);
    }

    @Transactional
    @Override
    public Long deleteById(Long commentId, UserDto userDto) {
        CommentEntity comment = this.commentRepository.findByIdAndUserId(commentId, userDto.getId());
        if (comment == null) {
            throw new ZooooException(ErrorCode.COMMENT_NOT_FOUND);
        }
        this.commentRepository.delete(comment);
        return commentId;
    }
}
