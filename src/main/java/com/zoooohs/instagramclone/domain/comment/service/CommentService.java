package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

import java.util.List;

public interface CommentService {
    CommentDto create(CommentDto commentDto, Long postId, UserDto userDto);

    List<CommentDto> getPostCommentList(Long postId, PageModel pageModel, Long userId);

    CommentDto updateComment(Long commentId, CommentDto commentDto, UserDto userDto);

    Long deleteById(Long commentId, UserDto userDto);
}
