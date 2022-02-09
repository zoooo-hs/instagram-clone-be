package com.zoooohs.instagramclone.domain.like.controller;

import com.zoooohs.instagramclone.domain.like.dto.LikeDto;
import com.zoooohs.instagramclone.domain.like.service.LikeService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public LikeDto like(@PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return likeService.like(postId, userDto);
    }

    @DeleteMapping("/post/{postId}/like")
    public Long unlike(@PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return likeService.unlike(postId, userDto);
    }
}
