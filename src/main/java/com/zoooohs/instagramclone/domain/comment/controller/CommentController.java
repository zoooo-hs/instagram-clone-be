package com.zoooohs.instagramclone.domain.comment.controller;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.service.CommentService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public CommentDto create(@RequestBody @Valid CommentDto commentDto, @PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        commentDto = commentService.create(commentDto, postId, userDto);
        return commentDto;
    }

}
