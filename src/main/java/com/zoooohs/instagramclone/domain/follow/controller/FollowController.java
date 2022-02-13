package com.zoooohs.instagramclone.domain.follow.controller;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.service.FollowService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/user/{followUserId}/follow")
    public FollowDto follow(@PathVariable Long followUserId, @AuthenticationPrincipal UserDto userDto) {
        return followService.follow(followUserId, userDto.getId());
    }

    @DeleteMapping("/user/{followUserId}/follow")
    public Long unfollow(@PathVariable Long followUserId, @AuthenticationPrincipal UserDto userDto) {
        return followService.unfollow(followUserId, userDto.getId());
    }
}
