package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

public interface CommentService {
    CommentDto create(CommentDto commentDto, Long postId, UserDto userDto);
}
