package com.zoooohs.instagramclone.domain.user.controller;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.service.UserService;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 상세 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공. 사용자 상세 정보 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.Info.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 사용자",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @GetMapping("/user/{userId}")
    public UserDto.Info getInfo(@PathVariable Long userId) {
        return this.userService.getInfo(userId);
    }

    @Operation(summary = "이름 기반 사용자 정보 조회", description = "특정 사용자의 상세 정보를 이름으로 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공. 사용자 상세 정보 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.Info.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 사용자",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @GetMapping("/name/{name}/user")
    public UserDto.Info findByName(@PathVariable String name, @AuthenticationPrincipal UserDto userDto) {
        return this.userService.findByName(name, userDto);
    }

    @Operation(summary = "사용자 리스트 조회", description = "사용자 리스트를 조회하거나, 이름으로 사용자 리스트 검색")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공. 사용자 리스트 조회 결과 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.Info.class))) }
            ),
    })
    @GetMapping("/user")
    public List<UserDto.Info> getUsers(@ModelAttribute @Valid SearchModel searchModel) {
        return userService.getUsers(searchModel);
    }

    // TODO: user Id를 받아서 작동하도록 변경해야함
    @Operation(summary = "사용자 바이오 수정", description = "자신의 바이오 정보를 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "수정 성공. 수정 정보 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.Info.class)) }
            ),
    })
    @PatchMapping("/user/{userId}/bio")
    public UserDto.Info updateBio(@PathVariable("userId") Long userId, @RequestBody UserDto.Info userDto, @AuthenticationPrincipal UserDto authUserDto) {
        userDto.setId(userId);
        return this.userService.updateBio(userDto, authUserDto);
    }

    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호 새로운 비밀번호를 입력받아, 새로운 비밀번호로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "변경 성공. 유저 정보 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.Info.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "유저 정보가 일치하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "동일한 비밀번호로 변경 시도",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PatchMapping("/user/{userId}/password")
    public UserDto.Info updatePassword(@PathVariable("userId") Long userId, @RequestBody UserDto.UpdatePassword passwordDto, @AuthenticationPrincipal UserDto authUserDto) {
        return userService.updatePassword(userId, passwordDto, authUserDto);
    }
}
