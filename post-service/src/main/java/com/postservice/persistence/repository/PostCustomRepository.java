package com.postservice.persistence.repository;

import com.postservice.dto.response.PostResponseDto;

public interface PostCustomRepository {

    PostResponseDto getPostDetailsById(Long postId);
}