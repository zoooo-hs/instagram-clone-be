package com.zoooohs.instagramclone.domain.photo.controller;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.service.PhotoService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/user/{userId}/photo")
    public PhotoDto.Photo uploadProfile(@PathVariable Long userId, @RequestPart("photo") MultipartFile photo, @AuthenticationPrincipal UserDto userDto) {
        if (!userId.equals(userDto.getId())) {
            throw new ZooooException(ErrorCode.USER_NOT_FOUND);
        }
        return photoService.uploadProfile(photo, userDto.getId());
    }
}
