package com.zoooohs.instagramclone.domain.hashtag.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.hashtag.dto.HashTagDto;
import com.zoooohs.instagramclone.domain.hashtag.dto.Search;

import java.util.List;

public interface HashTagService {
    List<String> extract(String content);

    List<HashTagDto> manage(String content, Long postId);

    List<Search> search(SearchModel searchModel);
}
