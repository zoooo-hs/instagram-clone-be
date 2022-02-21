package com.zoooohs.instagramclone.domain.hashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "HashTagSearchDTO")
@Data
@NoArgsConstructor
public class Search {
    @Schema(description = "tag 이름")
    private String tag;
    @Schema(description = "해쉬 태그가 사용되 게시글 개수")
    private Long count;

    @Builder
    public Search(String tag, Long count) {
        this.tag = tag;
        this.count = count;
    }
}
