package com.postservice.application;

import com.postservice.application.exception.PostNotFoundException;
import com.postservice.application.exception.WriterDifferentException;
import com.postservice.common.enums.PostType;
import com.postservice.dto.request.PostRequestDto;
import com.postservice.dto.request.PostUpdateRequestDto;
import com.postservice.dto.response.PostResponseDto;
import com.postservice.persistence.Post;
import com.postservice.persistence.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;

    @Mock
    PostRepository postRepository;

    Post post;

    @BeforeEach
    void beforeEach() {
        post = Post.create("writerId", 123L, "Test", "This is test", PostType.REPORT);
    }

    @Test
    @DisplayName("게시물 생성")
    void createPost() throws Exception {
        // given
        String writerId = "userId";
        Long teamId = 123L;
        PostRequestDto postRequestDto = new PostRequestDto("Test", "This is test", PostType.REPORT);

        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        Long result = postService.createPost(writerId, teamId, postRequestDto);

        // then
        assertDoesNotThrow(() -> result);
    }

    @Test
    @DisplayName("게시물 업데이트")
    void updateInfo() throws Exception {
        // given
        String writerId = "writerId";
        Long postId = 1L;
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto("Update Test", "This is update", PostType.NOTICE);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        Long result = postService.updateInfo(postId, writerId, postUpdateRequestDto);

        // then
        assertDoesNotThrow(() -> result);
    }

    @Test
    @DisplayName("게시물 업데이트 시 조회 실패")
    void updateInfoThrowPostNotFoundException() throws Exception {
        // given
        String writerId = "writerId";
        Long postId = 1L;
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto("Update Test", "This is update", PostType.NOTICE);

        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        // then
        assertThrows(PostNotFoundException.class, () -> postService.updateInfo(postId, writerId, postUpdateRequestDto));
    }

    @Test
    @DisplayName("게시물 업데이트 시 작성자 불일치")
    void updateInfoThrowWriterDifferentException() throws Exception {
        // given
        String userId = "anonymous";
        Long postId = 1L;
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto("Update Test", "This is update", PostType.NOTICE);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // then
        assertThrows(WriterDifferentException.class, () -> postService.updateInfo(postId, userId, postUpdateRequestDto));
    }

    @Test
    @DisplayName("게시물 상세 조회")
    void findPostDetailsById() throws Exception {
        // given
        Long postId = 1L;
        PostResponseDto postResponseDto = new PostResponseDto(
                postId, post.getTeamId(), post.getWriterId(), post.getTitle(), post.getContent(), post.getPostType(), post.getView(),
                post.getCreatedAt(), post.getModifiedAt());

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(postRepository.getPostDetailsById(post.getId())).willReturn(postResponseDto);

        // when
        PostResponseDto result = postService.findPostDetailsById(postId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.getId()).isEqualTo(postId);
        assertThat(result.getWriterId()).isEqualTo("writerId");
        assertThat(result.getTitle()).isEqualTo("Test");
        assertThat(result.getContent()).isEqualTo("This is test");
    }

    @Test
    @DisplayName("게시물 삭제")
    void deletePost() throws Exception {
        // given
        String writerId = "writerId";
        Long postId = 1L;

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // then
        assertDoesNotThrow(() -> postService.deletePost(postId, writerId));
    }
}