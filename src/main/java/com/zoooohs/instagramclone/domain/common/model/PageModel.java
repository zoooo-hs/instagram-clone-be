package com.zoooohs.instagramclone.domain.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PageModel {
    @Schema(description = "페이지 번호")
    protected int index;
    @Schema(description = "페이지 당 로우 개수")
    protected int size;

    @Builder
    public PageModel(int index, int size) {
        this.index = index;
        this.size = size;
    }
}
