package com.zoooohs.instagramclone.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    List<String> store(List<MultipartFile> files);
}
