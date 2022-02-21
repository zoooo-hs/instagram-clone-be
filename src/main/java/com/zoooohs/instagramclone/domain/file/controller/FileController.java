package com.zoooohs.instagramclone.domain.file.controller;

import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private PhotoService photoService;

    @Operation(hidden = true)
    @PostMapping("/file")
    public List<PhotoDto.Photo> upload(@RequestPart List<MultipartFile> files) {
        List<String> paths = this.storageService.store(files);
        return this.photoService.saveAll(paths);
    }
}
