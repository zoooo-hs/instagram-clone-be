package com.zoooohs.instagramclone.domain.photo.service;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;

import java.util.List;

public interface PhotoService {
    List<PhotoDto.Photo> saveAll(List<String> photoIds);
}
