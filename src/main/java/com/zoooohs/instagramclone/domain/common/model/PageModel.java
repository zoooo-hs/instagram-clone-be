package com.zoooohs.instagramclone.domain.common.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PageModel {
    protected int index;
    protected int size;

    @Builder
    public PageModel(int index, int size) {
        this.index = index;
        this.size = size;
    }
}
