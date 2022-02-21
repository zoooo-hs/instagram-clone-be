package com.zoooohs.instagramclone.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import com.zoooohs.instagramclone.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class S3StorageServiceImpl implements StorageService {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public List<String> store(List<MultipartFile> files) {
        return files.stream().map(multipartFile -> {
            String fileName = UUID.randomUUID() + FileUtils.getExtension(multipartFile.getOriginalFilename()); // random uuid name + extension
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            try (InputStream inputStream = multipartFile.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                // TODO: 더 나은 방버 찾기
                throw new ZooooException(ErrorCode.INTERNAL_ERROR);
            }
            return amazonS3Client.getUrl(bucketName, fileName).toString();
        }).collect(Collectors.toList());
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public void delete(String path) {
        // 없는 파일 삭제 호출 시 내부 로그라도 남겨야 할 것 같음
        if (exists(path)) {
            String fileName = getFileName(path);
            amazonS3Client.deleteObject(bucketName, fileName);
        }
    }

    @Override
    public Boolean exists(String path) {
        String fileName = getFileName(path);
        if (fileName == null) return false;
        return amazonS3Client.doesObjectExist(bucketName, fileName);
    }

    @Override
    public void deleteAll(List<String> paths) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
        List<DeleteObjectsRequest.KeyVersion> keys = paths.stream()
                .map(this::getFileName).map(DeleteObjectsRequest.KeyVersion::new).collect(Collectors.toList());
        request.setKeys(keys);
        try {
            amazonS3Client.deleteObjects(request);
        } catch (Exception e) {
            // TODO: AOP log
            e.printStackTrace();
        }
    }

    private String getFileName(String path) {
        String [] temp = path.split("amazonaws\\.com/");
        if (temp.length != 2) {
            temp = path.split(bucketName + "/");
        }
        if (temp.length != 2) {
            return null;
        }
        String fileName= temp[1];
        return fileName;
    }
}
