package com.zoooohs.instagramclone.domain.post.controller;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/post")
    public PostDto.Post create(@RequestParam("description") @NotNull String description, @RequestPart("files") List<MultipartFile> files, @AuthenticationPrincipal UserDto userDto) {
        PostDto.Post result = this.postService.create(PostDto.Post.builder().description(description).build(), files, userDto);
        return result;
    }

    @GetMapping("/post")
    public List<PostDto.Post> getFeeds(@ModelAttribute PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.getFeeds(userDto.getId(), pageModel);
    }

    @GetMapping("/user/{userId}/post")
    public List<PostDto.Post> findAllByUserId(@PathVariable Long userId, @ModelAttribute PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.findByUserId(userId, pageModel, userDto.getId());
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
