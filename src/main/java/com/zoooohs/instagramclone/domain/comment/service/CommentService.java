package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

import java.util.List;

public interface CommentService {
    CommentDto createPostComment(CommentDto commentDto, Long postId, UserDto userDto);

    CommentDto createCommentComment(CommentDto commentDto, Long commentId, UserDto userDto);

    List<CommentDto> getPostCommentList(Long postId, PageModel pageModel, Long userId);

    List<CommentDto> getCommentCommentList(Long commentId, PageModel pageModel, Long id);

    CommentDto updateComment(Long commentId, CommentDto commentDto, UserDto userDto);

    Long deleteById(Long commentId, UserDto userDto);
}
