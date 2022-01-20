package com.zoooohs.instagramclone.domain.user.controller;

import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{userId}")
    public UserDto.Info getInfo(@PathVariable Long userId) {
        return this.userService.getInfo(userId);
    }

    @PatchMapping("/user/bio")
    public UserDto.Info updateBio(@RequestBody UserDto.Info userDto, @AuthenticationPrincipal UserDto authUserDto) {
        return this.userService.updateBio(userDto.getBio(), authUserDto);
    }
}
