package com.zoooohs.instagramclone.domain.post.controller;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/post")
    public PostDto.Post create(@RequestBody @Valid PostDto.Post postDto, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.create(postDto, userDto);
    }
}
