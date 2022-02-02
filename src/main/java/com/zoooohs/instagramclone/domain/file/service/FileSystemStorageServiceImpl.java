package com.zoooohs.instagramclone.domain.file.service;

import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import com.zoooohs.instagramclone.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileSystemStorageServiceImpl implements StorageService {

    @Value("${storage.filesystem}")
    private String storagePath;

    @Override
    public List<String> store(List<MultipartFile> files) {
        return files.stream().map(multipartFile -> {
            String path = storagePath + UUID.randomUUID() + FileUtils.getExtension(multipartFile.getOriginalFilename());
            File file = new File(path);
            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                // TODO: internal error 잡는 더 나은 방법 찾기
                throw new ZooooException(ErrorCode.INTERNAL_ERROR);
            }
            return path;
        }).collect(Collectors.toList());
    }

    public String getStoragePath() {
        return storagePath;
    }
}
