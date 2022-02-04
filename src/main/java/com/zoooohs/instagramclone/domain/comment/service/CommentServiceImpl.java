package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
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
}
