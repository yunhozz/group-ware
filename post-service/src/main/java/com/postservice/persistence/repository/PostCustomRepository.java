package com.postservice.persistence.repository;

import com.postservice.common.enums.PostType;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;

import java.util.List;

public interface PostCustomRepository {

    PostDetailsQueryDto getPostDetailsById(Long postId);
    List<PostSimpleQueryDto> getPostSimpleListByType(PostType postType);
}