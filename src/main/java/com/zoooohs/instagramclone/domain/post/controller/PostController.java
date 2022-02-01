package com.zoooohs.instagramclone.domain.post.controller;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/post")
    public PostDto.Post create(@RequestBody @Valid PostDto.Post postDto, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.create(postDto, userDto);
    }

    @GetMapping("/post")
    public List<PostDto.Post> read(@ModelAttribute PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.findAllExceptSelf(userDto.getId(), pageModel);
    }

    @GetMapping("/user/{userId}/post")
    public List<PostDto.Post> findAllByUserId(@PathVariable Long userId, @ModelAttribute PageModel pageModel) {
        return this.postService.findByUserId(userId, pageModel);
    }

    @PatchMapping("/post/{postId}/description")
    public PostDto.Post updateDescription(@PathVariable Long postId, @RequestBody PostDto.Post post, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.updateDescription(postId, post, userDto);
    }

    @DeleteMapping("/post/{postId}")
    public Long deleteById(@PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.deleteById(postId, userDto.getId());
    }
}
