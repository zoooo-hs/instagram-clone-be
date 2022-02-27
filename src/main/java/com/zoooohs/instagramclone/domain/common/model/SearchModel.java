package com.zoooohs.instagramclone.domain.common.model;

import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.common.type.SortKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SearchModel extends PageModel {
    @Schema(description = "검색 키워드")
    private String keyword;
    @NotNull
    @Schema(name = "searchKey", description = "검색 종류. (이름, 해쉬 태그, ...)")
    private SearchKeyType searchKey;

    public SearchModel(int index, int size, SortKeyType sortKey, String keyword, SearchKeyType searchKey) {
        super(index, size, sortKey);
        this.keyword = keyword;
        this.searchKey = searchKey;
    }
}
