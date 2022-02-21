package com.zoooohs.instagramclone.domain.post.controller;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "새로운 개시글 작성.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "작성 성공. 게시글 작성 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.Post.class)) }
            ),
            @ApiResponse(
                    responseCode = "400", description = "이미지가 아닌 데이터 업로드",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public PostDto.Post create(@RequestParam("description") @NotNull String description,
                               @Schema(description = "사진 MultipartFile List")
                               @RequestPart("files") List<MultipartFile> files,
                               @AuthenticationPrincipal UserDto userDto) {
        PostDto.Post result = this.postService.create(PostDto.Post.builder().description(description).build(), files, userDto);
        return result;
    }

    @Operation(summary = "게시글 피드 불러오기", description = "팔로잉 중인 사람들의 게시글 혹은 해쉬태그로 검색한 게시글 피드를 불러온다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "피드 불러오기 성공. 게시글 리스트 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.Post.class))) }
            ),
    })
    @GetMapping("/post")
    public List<PostDto.Post> getFeeds(@ModelAttribute SearchModel searchModel, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.getFeeds(userDto.getId(), searchModel);
    }

    @Operation(summary = "특정 사용자의 게시글 불러오기", description = "특정 사용자의 게시글 리스트를 불러온다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "불러오기 성공. 게시글 리스트 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.Post.class))) }
            ),
    })
    @GetMapping("/user/{userId}/post")
    public List<PostDto.Post> findAllByUserId(@PathVariable Long userId, @ModelAttribute PageModel pageModel, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.findByUserId(userId, pageModel, userDto.getId());
    }

    @Operation(summary = "게시글 설명 수정", description = "게시글의 설명을 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "작성 성공. 게시글 작성 결과 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.Post.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 게시글",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PatchMapping("/post/{postId}/description")
    public PostDto.Post updateDescription(@PathVariable Long postId, @RequestBody PostDto.Post post, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.updateDescription(postId, post, userDto);
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "삭제 성공. 게시글 ID 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 게시글",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @DeleteMapping("/post/{postId}")
    public Long deleteById(@PathVariable Long postId, @AuthenticationPrincipal UserDto userDto) {
        return this.postService.deleteById(postId, userDto.getId());
    }
}
