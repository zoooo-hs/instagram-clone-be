package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final ModelMapper modelMapper;

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
}
