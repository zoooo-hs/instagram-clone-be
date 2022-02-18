package com.zoooohs.instagramclone.domain.common.model;

import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SearchModel extends PageModel {
    private String keyword;
    @NotNull
    private SearchKeyType searchKey;

    public SearchModel(int index, int size, String keyword, SearchKeyType searchKey) {
        super(index, size);
        this.keyword = keyword;
        this.searchKey = searchKey;
    }
}
