package com.zoooohs.instagramclone.domain.like.controller;

import com.zoooohs.instagramclone.domain.like.dto.CommentLikeDto;
import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.service.LikeService;
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
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "게시글 좋아요", description = "특정 게시글 좋아요.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "좋아요 성공. 좋아요 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PostLikeDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "이미 좋아요한 게시글",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/post/{postId}/like")
    public PostLikeDto likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return likeService.likePost(postId, userDto);
    }

    @Operation(summary = "댓글 좋아요", description = "특정 댓글 좋아요.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "좋아요 성공. 좋아요 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CommentLikeDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "이미 좋아요한 댓글",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/comment/{commentId}/like")
    public CommentLikeDto likeComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDto userDto) {
        return likeService.likeComment(commentId, userDto);
    }

    @Operation(summary = "통합 좋아요 취소", description = "댓글, 게시글 좋아요 id로 취소. [주의] 1.0.0 버전의 DELETE /post/{postId}/like, DELETE /comment/{commentId}/like 는 제거 되었습니다. 본 API 로 통합되었습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "좋아요 취소 성공. 좋아요 ID 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "좋아요를 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @DeleteMapping("/like/{likeId}")
    public Long unlike(@PathVariable Long likeId, @AuthenticationPrincipal UserDto userDto) {
        return likeService.unlike(likeId, userDto);
    }
}
