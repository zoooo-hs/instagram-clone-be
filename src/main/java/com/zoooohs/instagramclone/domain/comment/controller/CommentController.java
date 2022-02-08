package com.zoooohs.instagramclone.domain.comment.controller;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.service.CommentService;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public CommentDto create(@RequestBody @Valid CommentDto commentDto, @PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return commentService.create(commentDto, postId, userDto);
    }

    @GetMapping("/post/{postId}/comment")
    public List<CommentDto> getPostCommentList(@PathVariable Long postId, @ModelAttribute @NotNull PageModel pageModel) {
        return commentService.getPostCommentList(postId, pageModel);
    }

    @PatchMapping("/comment/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto, @AuthenticationPrincipal UserDto userDto) {
        return commentService.updateComment(commentId, commentDto, userDto);
    }

    @DeleteMapping("/comment/{commentId}")
    public Long deleteById(@PathVariable Long commentId, @AuthenticationPrincipal UserDto userDto) {
        Long re =  commentService.deleteById(commentId, userDto);
        return re;
    }

}
