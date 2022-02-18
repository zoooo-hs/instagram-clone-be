package com.zoooohs.instagramclone.domain.hashtag.service;

import com.zoooohs.instagramclone.domain.hashtag.dto.HashTagDto;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.hashtag.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<String> extract(String content) { // private 으로 돌려도 되지 않나
        Pattern hashTagPattern = Pattern.compile("#([\\w\\d-_][\\w\\d-_]*)");
        Matcher matcher = hashTagPattern.matcher(content);

        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    @Override
    public List<HashTagDto> manage(String content, Long postId) {
        List<HashTagEntity> hashTagEntities = Optional.ofNullable(postId).map(hashTagRepository::findByPostId).orElse(List.of());
        List<String> tagsInDB = hashTagEntities.stream().map(HashTagEntity::getTag).collect(Collectors.toList()); // db 에 있는 tag
        List<String> tagsInContent = this.extract(content); // content 에서 찾은 tag

        // content 에서 뽑은 tag 중 db 에 없는 tag 면 새로 저장할 tag
        List<HashTagEntity> willSave = tagsInContent.stream()
                .filter(Predicate.not(tagsInDB::contains))
                .map(t -> HashTagEntity.builder().tag(t).build()).collect(Collectors.toList());
        // db 에 남아 있을 tag
        List<HashTagEntity> willRemain = hashTagEntities.stream()
                .filter(entity -> tagsInContent.contains(entity.getTag())).collect(Collectors.toList());

        willSave.addAll(willRemain);
        return willSave.stream().map(entity -> modelMapper.map(entity, HashTagDto.class)).collect(Collectors.toList());
    }
}
