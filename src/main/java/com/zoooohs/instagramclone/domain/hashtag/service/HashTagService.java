package com.zoooohs.instagramclone.domain.hashtag.service;

import com.zoooohs.instagramclone.domain.hashtag.dto.HashTagDto;

import java.util.List;

public interface HashTagService {
    List<String> extract(String content);

    List<HashTagDto> manage(String content, Long postId);
}
