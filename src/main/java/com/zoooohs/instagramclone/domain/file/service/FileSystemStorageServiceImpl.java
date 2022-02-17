package com.zoooohs.instagramclone.domain.file.service;

import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class FileSystemStorageServiceImpl implements StorageService {

    @Value("${storage.filesystem}")
    private String bucketName;

    @Override
    public List<String> store(List<MultipartFile> files) {
        return files.stream().map(multipartFile -> {
            String path = bucketName + multipartFile.getOriginalFilename();
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

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public void delete(String path) {
        if (exists(path)) {
            new File(path).delete();
        }
    }

    @Override
    public Boolean exists(String path) {
        return new File(path).exists();
    }
}
