package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    List<PhotoDto.Photo> saveAll(List<String> photoIds);

    PhotoDto.Photo uploadProfile(MultipartFile photo, Long userId);
}
