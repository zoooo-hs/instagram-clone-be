package com.zoooohs.instagramclone.domain.photo.controller;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.service.PhotoService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @Operation(summary = "사용자 프로필 사진 변경", description = "사용자의 프로필 사진을 업로드하여 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "프로필 사진 변경 성공. 사진 변경 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PhotoDto.Photo.class)) }
            ),
            @ApiResponse(
                    responseCode = "400", description = "사진이 아닌 데이터를 업로드",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping(value = "/user/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoDto.Photo uploadProfile(@PathVariable Long userId,
                                        @Schema(description = "사진 MultipartFile")
                                        @RequestPart("photo") MultipartFile photo,
                                        @AuthenticationPrincipal UserDto userDto) {
        if (!userId.equals(userDto.getId())) {
            throw new ZooooException(ErrorCode.USER_NOT_FOUND);
        }
        return photoService.uploadProfile(photo, userDto.getId());
    }
}
