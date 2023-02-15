package com.postservice.application;

import com.postservice.application.exception.PostNotFoundException;
import com.postservice.application.exception.WriterDifferentException;
import com.postservice.dto.request.PostRequestDto;
import com.postservice.dto.request.PostUpdateRequestDto;
import com.postservice.dto.response.PostResponseDto;
import com.postservice.persistence.Post;
import com.postservice.persistence.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long createPost(String writerId, Long teamId, PostRequestDto postRequestDto) {
        Post post = Post.create(writerId, teamId, postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getPostType());
        return postRepository.save(post).getId();
    }

    @Transactional
    public Long updateInfo(Long postId, String userId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = findPost(postId);
        validateUserIsWriter(post, userId);
        return post.updateInfo(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent(), postUpdateRequestDto.getPostType());
    }

    @Transactional
    public PostResponseDto findPostDetailsById(Long id) {
        Post post = findPost(id);
        post.addView();
        return postRepository.getPostDetailsById(post.getId());
    }

    @Transactional
    public void deletePost(Long postId, String userId) {
        Post post = findPost(postId);
        validateUserIsWriter(post, userId);
        post.delete();
    }

    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validateUserIsWriter(Post post, String userId) {
        if (!post.isUserIsWriter(userId)) {
            throw new WriterDifferentException();
        }
    }
}