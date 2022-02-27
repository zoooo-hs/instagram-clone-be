package com.zoooohs.instagramclone.domain.common.model;

import com.zoooohs.instagramclone.domain.common.type.SortKeyType;
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
    @Schema(description = "정렬 키워드")
    private SortKeyType sortKey;

    @Builder
    public PageModel(int index, int size, SortKeyType sortKey) {
        this.index = index;
        this.size = size;
        this.sortKey = sortKey;
    }
}
