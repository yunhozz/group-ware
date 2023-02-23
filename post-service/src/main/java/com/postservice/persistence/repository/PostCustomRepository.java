package com.postservice.persistence.repository;

import com.postservice.common.enums.PostType;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PostCustomRepository {

    Optional<PostDetailsQueryDto> getPostDetailsById(Long postId);
    List<PostSimpleQueryDto> getPostSimpleListByTypeAndTeamId(PostType postType, Long teamId);
    Slice<PostSimpleQueryDto> getPostSimpleSliceByTypeAndTeamId(PostType postType, Long teamId, Long cursorId, Pageable pageable);
}