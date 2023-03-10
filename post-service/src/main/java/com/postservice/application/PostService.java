package com.postservice.application;

import com.postservice.application.exception.FileUploadFailException;
import com.postservice.application.exception.PostNotFoundException;
import com.postservice.application.exception.WriterDifferentException;
import com.postservice.common.enums.PostType;
import com.postservice.common.util.RandomIdUtils;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import com.postservice.dto.request.PostRequestDto;
import com.postservice.dto.request.PostUpdateRequestDto;
import com.postservice.persistence.FileEntity;
import com.postservice.persistence.Post;
import com.postservice.persistence.repository.CommentRepository;
import com.postservice.persistence.repository.FileRepository;
import com.postservice.persistence.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;

    @Value("${file.directory}")
    private String fileDir;

    @Transactional
    public Long createPost(String writerId, Long teamId, PostRequestDto postRequestDto) {
        Post post = Post.create(writerId, teamId, postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getPostType());
        List<MultipartFile> files = postRequestDto.getFiles();
        saveFiles(post, files);

        return postRepository.save(post).getId();
    }

    @Transactional
    public Long updateInfo(Long postId, String userId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = findPost(postId);
        validateUserIsWriter(post, userId);
        post.updateInfo(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent(), postUpdateRequestDto.getPostType());

        deleteFiles(post.getId()); // ?????? ?????? ????????? ?????? (bulk)
        List<MultipartFile> files = postUpdateRequestDto.getFiles();
        saveFiles(post, files);

        return post.getId();
    }

    @Transactional
    public PostDetailsQueryDto findPostDetailsById(Long id) {
        Post post = findPost(id);
        post.addView();
        return postRepository.getPostDetailsById(post.getId())
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public void deletePost(Long postId, String userId) {
        Post post = findPost(postId);
        validateUserIsWriter(post, userId);
        post.delete();

        deleteComments(postId); // ???????????? ?????? ????????? ?????? (bulk)
        deleteFiles(postId); // ?????? ?????? ????????? ?????? (bulk)
    }

    @Transactional(readOnly = true)
    public Slice<PostSimpleQueryDto> findSimpleSliceDto(PostType postType, Long teamId, Long cursorId, Pageable pageable) {
        return postRepository.getPostSimpleSliceByTypeAndTeamId(postType, teamId, cursorId, pageable);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleQueryDto> findSimpleListDto(PostType postType, Long teamId) {
        return postRepository.getPostSimpleListByTypeAndTeamId(postType, teamId);
    }

    @Transactional
    protected void deleteComments(Long postId) {
        commentRepository.deleteCommentsByPostId(postId);
    }

    @Transactional
    protected void deleteFiles(Long postId) {
        fileRepository.deleteFilesByPostId(postId);
    }

    private void saveFiles(Post post, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            try {
                List<FileEntity> fileEntityList = new ArrayList<>();
                for (MultipartFile file : files) {
                    String originalName = file.getOriginalFilename();
                    String extension = originalName.substring(originalName.lastIndexOf("."));
                    String saveName = UUID.randomUUID() + extension;
                    String savePath = fileDir + saveName;

                    FileEntity fileEntity = FileEntity.builder()
                            .post(post)
                            .fileId(RandomIdUtils.generateFileId())
                            .originalName(originalName)
                            .saveName(saveName)
                            .savePath(savePath)
                            .build();
                    fileEntityList.add(fileEntity);
                    file.transferTo(new File(savePath));
                }

                fileRepository.saveAll(fileEntityList);

            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                throw new FileUploadFailException();
            }
        }
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