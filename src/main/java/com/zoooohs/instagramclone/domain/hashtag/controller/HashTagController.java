package com.zoooohs.instagramclone.domain.hashtag.controller;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.hashtag.dto.Search;
import com.zoooohs.instagramclone.domain.hashtag.service.HashTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HashTagController {

    private final HashTagService hashTagService;

    @Operation(summary = "해쉬 태그 검색", description = "비슷한 단어의 해쉬태그를 검색 한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "검색 성공. 해쉬태그 리스트 반환",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Search.class))) }
            ),
    })
    @GetMapping("/hash-tag")
    public List<Search> search(@ModelAttribute SearchModel searchModel) {
        return hashTagService.search(searchModel);
    }
}
