package com.zoooohs.instagramclone.domain.comment.controller;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.service.CommentService;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "게시글 댓글 작성", description = "게시글 댓글을 작성한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "게시글 댓글 작성 성공. 작성 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "게시글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/post/{postId}/comment")
    public CommentDto createPostComment(@RequestBody @Valid CommentDto commentDto, @PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return commentService.createPostComment(commentDto, postId, userDto);
    }

    @Operation(summary = "대댓글 작성", description = "대댓글을 작성한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "대댓글 작성 성공. 작성 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "원댓글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/comment/{commentId}/comment")
    public CommentDto createCommentComment(@RequestBody @Valid CommentDto commentDto, @PathVariable Long commentId, @AuthenticationPrincipal UserDto userDto) {
        return commentService.createCommentComment(commentDto, commentId, userDto);
    }

    @Operation(summary = "게시글 댓글 조회", description = "게시글 댓글을 리스트를 불러온다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공. 댓글 리스트 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "게시글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @GetMapping("/post/{postId}/comment")
    public List<CommentDto> getPostCommentList(@PathVariable Long postId, @ModelAttribute @NotNull PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return commentService.getPostCommentList(postId, pageModel, userDto.getId());
    }

    @Operation(summary = "대댓글 조회", description = "대댓글 리스트를 불러온다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공. 댓글 리스트 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "게시글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @GetMapping("/comment/{commentId}/comment")
    public List<CommentDto> getCommentCommentList(@PathVariable Long commentId, @ModelAttribute @NotNull PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return commentService.getCommentCommentList(commentId, pageModel, userDto.getId());
    }

    @Operation(summary = "댓글 수정", description = "댓글의 내용을 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "수정 성공. 수정 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "댓글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PatchMapping("/comment/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto, @AuthenticationPrincipal UserDto userDto) {
        return commentService.updateComment(commentId, commentDto, userDto);
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "삭제 성공. 삭제된 댓글 ID 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "댓글이 존재하지 않음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @DeleteMapping("/comment/{commentId}")
    public Long deleteById(@PathVariable Long commentId, @AuthenticationPrincipal UserDto userDto) {
        return commentService.deleteById(commentId, userDto);
    }

}
