package com.zoooohs.instagramclone.domain.hashtag.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Search {
    private String tag;
    private Long count;

    @Builder
    public Search(String tag, Long count) {
        this.tag = tag;
        this.count = count;
    }
}
