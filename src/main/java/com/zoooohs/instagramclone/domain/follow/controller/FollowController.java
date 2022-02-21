package com.zoooohs.instagramclone.domain.follow.controller;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.service.FollowService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "팔로우", description = "특정 유저를 팔로우 한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "팔로우 성공. 팔로우 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = FollowDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "유저를 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "자신을 팔로우",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "이미 팔로우하고 있음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/user/{followUserId}/follow")
    public FollowDto follow(@PathVariable Long followUserId, @AuthenticationPrincipal UserDto userDto) {
        return followService.follow(followUserId, userDto.getId());
    }

    @Operation(summary = "언팔로우", description = "특정 유저를 언팔로우 한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "언팔로우 성공. 팔로우 ID 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "팔로우 정보를 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "자신을 팔로우",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @DeleteMapping("/user/{followUserId}/follow")
    public Long unfollow(@PathVariable Long followUserId, @AuthenticationPrincipal UserDto userDto) {
        return followService.unfollow(followUserId, userDto.getId());
    }
}
