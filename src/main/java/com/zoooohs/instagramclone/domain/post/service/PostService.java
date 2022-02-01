package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;

import java.util.List;

public interface PostService {
    public PostDto.Post create(PostDto.Post postDto, UserDto userDto);

    // 자신의 게시물을 제외한 모든 게시물 가져오기
    public List<PostDto.Post> findAllExceptSelf(Long userId, PageModel pageModel);

    public List<PostDto.Post> findByUserId(Long userId, PageModel pageModel);

    PostDto.Post updateDescription(Long postId, PostDto.Post post, UserDto userDto);

    Long deleteById(Long postId, Long userId);
}
