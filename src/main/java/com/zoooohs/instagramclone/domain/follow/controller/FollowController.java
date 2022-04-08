package com.zoooohs.instagramclone.domain.follow.controller;

import java.util.List;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.service.FollowService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로잉 리스트 조회", description = "특정 유저의 팔로잉 리스트를 조회 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공. 팔로우 리스트 반환", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.Info.class))) }),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }),
    })
    @GetMapping("/user/{userId}/follow/follow-user")
    public List<UserDto.Info> findByUserId(@PathVariable Long userId, @AuthenticationPrincipal UserDto userDto) {
        return followService.findByUserId(userId, userDto);
    }

    @Operation(summary = "팔로워 리스트 조회", description = "특정 유저를 팔로우하는 유저 리스트를 조회 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공. 팔로워 리스트 반환", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.Info.class))) }),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }),
    })
    @GetMapping("/follow-user/{userId}/follow/user")
    public List<UserDto.Info> findByFollowUserId(@PathVariable Long userId, @AuthenticationPrincipal UserDto userDto) {
        return followService.findByFollowUserId(userId, userDto);
    }

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
