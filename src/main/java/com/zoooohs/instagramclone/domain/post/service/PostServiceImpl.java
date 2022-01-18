package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public PostDto.Post create(PostDto.Post postDto, UserDto userDto) {
        // TODO: file upload and dto -> file
        UserEntity user = this.modelMapper.map(userDto, UserEntity.class);
        PostEntity post = this.modelMapper.map(postDto, PostEntity.class);
        post.setUser(user);
        this.postRepository.save(post);
        return this.modelMapper.map(post, PostDto.Post.class);
    }

    @Override
    public List<PostDto.Post> read(PageModel pageModel, UserDto userDto) {
        Pageable pageable = PageRequest.of(pageModel.getIndex(), pageModel.getSize());
        List<PostEntity> postEntities = this.postRepository.findAllExceptUserId(userDto.getId(), pageable);
        return postEntities.stream().map(entity -> modelMapper.map(entity, PostDto.Post.class)).collect(Collectors.toList());
    }
}
