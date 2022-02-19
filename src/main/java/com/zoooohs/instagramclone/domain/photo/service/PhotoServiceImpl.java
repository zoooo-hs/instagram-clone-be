package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.photo.repository.PhotoRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public List<PhotoDto.Photo> saveAll(List<String> photoIds) {
        List<PhotoEntity> photoEntities = photoIds.stream().map(id -> {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPath(id);
            return photoEntity;
        }).collect(Collectors.toList());

        photoEntities = this.photoRepository.saveAll(photoEntities);

        return photoEntities.stream()
                .map(entity -> this.modelMapper.map(entity, PhotoDto.Photo.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PhotoDto.Photo uploadProfile(MultipartFile photo, Long userId) {
        if (!photo.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) && !photo.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
            throw new ZooooException(ErrorCode.INVALID_FILE_TYPE);
        }
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        Optional<PhotoEntity> oldPhoto = Optional.ofNullable(user.getPhoto());
        return Optional.ofNullable(storageService.store(List.of(photo))).map(this::saveAll).map(photos -> photos.get(0)).map(photoDto -> {
            user.setPhoto(modelMapper.map(photoDto, PhotoEntity.class));
            userRepository.save(user);
            oldPhoto.ifPresent(entity -> {
                String oldPath = entity.getPath();
                photoRepository.delete(entity);
                storageService.delete(oldPath);
            });
            return photoDto;
        }).orElse(null);
    }
}
