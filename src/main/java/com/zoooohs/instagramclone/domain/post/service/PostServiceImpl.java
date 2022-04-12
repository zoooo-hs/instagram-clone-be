package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.hashtag.service.HashTagService;
import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.dto.PostDto.Post;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final HashTagService hashTagService;

    @Transactional
    @Override
    public PostDto.Post create(PostDto.Post postDto, List<MultipartFile> files, UserDto userDto) {
        // 이미지가 아니면 throw
        files.stream().map(MultipartFile::getContentType).filter(Predicate.not(this::isImage)).findAny().ifPresent(e -> {
            throw new ZooooException(ErrorCode.INVALID_FILE_TYPE);
        });
        List<String> photoPaths = this.storageService.store(files);
        Set<PhotoEntity> photos = photoPaths.stream().map(path -> PhotoEntity.builder().path(path).build()).collect(Collectors.toSet());
        UserEntity user = this.modelMapper.map(userDto, UserEntity.class);
        PostEntity post = this.modelMapper.map(postDto, PostEntity.class);
        post.setUser(user);
        post.setPhotos(photos);
        post.setHashTags(getHashTagEntities(post.getDescription(), post.getId()));
        post = this.postRepository.save(post);
        return this.modelMapper.map(post, PostDto.Post.class);
    }

    private boolean isImage(String fileType) {
        return fileType.equals(MediaType.IMAGE_JPEG_VALUE) || fileType.equals(MediaType.IMAGE_PNG_VALUE);
    }

    @Override
    public List<PostDto.Post> findAllExceptSelf(Long userId, PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getIndex(), pageModel.getSize());
        List<PostEntity> postEntities = this.postRepository.findAllExceptUserId(userId, pageable);
        return postEntities.stream().map(entity -> modelMapper.map(entity, PostDto.Post.class)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostDto.Post> findByUserId(Long postUserId, PageModel pageModel, Long userId) {
        List<PostEntity> postEntities = this.postRepository.findByUserId(postUserId, PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
        return makePostDto(postEntities, userId);
    }

    @Transactional
    @Override
    public PostDto.Post updateDescription(Long postId, PostDto.Post post, UserDto userDto) {
        PostEntity postEntity = this.postRepository.findById(postId).orElseThrow(() -> new ZooooException(ErrorCode.POST_NOT_FOUND));
        if (!postEntity.getUser().getId().equals(userDto.getId())) {
            throw new ZooooException(ErrorCode.POST_NOT_FOUND);
        }
        postEntity.setDescription(post.getDescription());
        postEntity.setHashTags(getHashTagEntities(post.getDescription(), postId ));
        postEntity = this.postRepository.save(postEntity);
        PostDto.Post result = this.modelMapper.map(postEntity, PostDto.Post.class);
        return result;
    }

    @Transactional
    @Override
    public Long deleteById(Long postId, Long userId) {
        PostEntity postEntity = this.postRepository.findByIdAndUserId(postId, userId);
        if (postEntity == null) {
            throw new ZooooException(ErrorCode.POST_NOT_FOUND);
        }
        List<String> photoPaths = postEntity.getPhotos().stream().map(PhotoEntity::getPath).collect(Collectors.toList());
        this.postRepository.delete(postEntity);
        photoPaths.stream().forEach(storageService::delete);
        return postId;
    }

    @Override
    public List<PostDto.Post> getFeeds(Long userId, SearchModel searchModel) {
        List<PostEntity> postEntities = null;
        if (searchModel.getKeyword() == null) {
           List<Long> userIds = followRepository.findByUserId(userId).stream().map(entity -> entity.getFollowUser().getId()).collect(Collectors.toList());
           userIds.add(userId);
           postEntities = postRepository.findAllByUserId(userIds, PageRequest.of(searchModel.getIndex(), searchModel.getSize()));
        } else {
            if (searchModel.getSearchKey().equals(SearchKeyType.HASH_TAG)) {
                postEntities = postRepository.findAllByTag(searchModel.getKeyword(), PageRequest.of(searchModel.getIndex(), searchModel.getSize()));
            }
        }
        return makePostDto(postEntities, userId);
    }

    private List<PostDto.Post> makePostDto(List<PostEntity> postEntities, Long userId) {
        return Optional.ofNullable(postEntities).map(Collection::stream).map(stream -> stream.map(entity -> {
            PostDto.Post post = this.modelMapper.map(entity, PostDto.Post.class);
            Long likedId = entity.getLikes().stream().filter(like -> like.getUser().getId().equals(userId)).findFirst().map(LikeEntity::getId).orElse(null);
            post.setLikedId(likedId);
            post.isLiked(likedId != null);
            post.getPhotos().sort(new Comparator<PhotoDto.Photo>() {
                @Override
                public int compare(PhotoDto.Photo o1, PhotoDto.Photo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return post;
        }).collect(Collectors.toList())).orElse(List.of());
    }

    private Set<HashTagEntity> getHashTagEntities(String content, Long postId) {
        return hashTagService.manage(content, postId).stream()
                .map(dto -> modelMapper.map(dto, HashTagEntity.class)).collect(Collectors.toSet());
    }

	@Override
	public List<Post> findByUserName(String userName, PageModel pageModel, Long userId) {
        List<PostEntity> postEntities = postRepository.findByUserName(userName, PageRequest.of(pageModel.getIndex(), pageModel.getSize()));
		return makePostDto(postEntities, userId);
	}
}
