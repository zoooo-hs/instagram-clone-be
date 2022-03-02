package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentCommentRepository;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.comment.repository.PostCommentRepository;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
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
    private final PostCommentRepository postCommentRepository;
    private final CommentCommentRepository commentCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentDto createPostComment(CommentDto commentDto, Long postId, UserDto userDto) {
        UserEntity user = userRepository.getById(userDto.getId());
        PostEntity post = this.postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        PostCommentEntity comment = this.modelMapper.map(commentDto, PostCommentEntity.class);
        comment.setPost(post);
        comment.setUser(user);
        comment = this.postCommentRepository.save(comment);
        return this.modelMapper.map(comment, CommentDto.class);
    }

    @Override
    public CommentDto createCommentComment(CommentDto commentDto, Long commentId, UserDto userDto) {
        UserEntity user = userRepository.getById(userDto.getId());
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new ZooooException(ErrorCode.COMMENT_NOT_FOUND));
        CommentCommentEntity commentComment = CommentCommentEntity.builder().user(user).comment(comment).content(commentDto.getContent()).build();
        commentComment = commentCommentRepository.save(commentComment);
        return this.modelMapper.map(commentComment, CommentDto.class);
    }

    @Override
    public List<CommentDto> getPostCommentList(Long postId, PageModel pageModel, Long userId) {
        PostEntity post = this.postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        List<PostCommentEntity> comments;
        if (pageModel.getSortKey() == null) {
            comments = this.postCommentRepository.findByPostId(post.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
        }
        else {
            switch (pageModel.getSortKey()) {
                case LIKE:
                    comments = postCommentRepository.findPostCommentsOrderByLikesSize(post.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
                    break;
                case COMMENT:
                    comments = postCommentRepository.findPostCommentsOrderByCommentsSize(post.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
                    break;
                default:
                    comments = this.postCommentRepository.findByPostId(post.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
            }
        }
        return comments.stream().map(entity -> {
            CommentEntity commentEntity = entity;
            return makeCommentDto(userId, commentEntity);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentCommentList(Long commentId, PageModel pageModel, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new ZooooException(ErrorCode.COMMENT_NOT_FOUND));
        List<CommentCommentEntity> comments;
        if (pageModel.getSortKey() == null) {
            comments = this.commentCommentRepository.findByCommentId(comment.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
        }
        else {
            switch (pageModel.getSortKey()) {
                case LIKE:
                    comments = commentCommentRepository.findCommentCommentsOrderByLikesSize(comment.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
                    break;
                case COMMENT:
                    comments = commentCommentRepository.findCommentCommentsOrderByCommentsSize(comment.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
                    break;
                default:
                    comments = this.commentCommentRepository.findByCommentId(comment.getId(), PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
            }
        }
        return comments.stream().map(entity -> {
            CommentEntity commentEntity = entity;
            return makeCommentDto(userId, commentEntity);
        }).collect(Collectors.toList());
    }

    private CommentDto makeCommentDto(Long userId, CommentEntity commentEntity) {
        CommentDto dto = this.modelMapper.map(commentEntity, CommentDto.class);
        boolean isLiked = commentEntity.getLikes().stream().filter(like -> like.getUser().getId().equals(userId)).findFirst().isPresent();
        dto.isLiked(isLiked);
        return dto;
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
