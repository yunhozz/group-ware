package com.postservice.application;

import com.postservice.application.exception.CommentNotFoundException;
import com.postservice.application.exception.PostNotFoundException;
import com.postservice.application.exception.WriterDifferentException;
import com.postservice.persistence.Comment;
import com.postservice.persistence.Post;
import com.postservice.persistence.repository.CommentRepository;
import com.postservice.persistence.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long makeParent(String writerId, Long postId, String content) {
        Post post = findPost(postId);
        Comment comment = Comment.create(writerId, post, content);
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long makeChild(String writerId, Long postId, Long parentId, String content) {
        Post post = findPost(postId);
        Comment parent = findComment(parentId);
        Comment child = Comment.createChild(writerId, post, content, parent);

        return commentRepository.save(child).getId();
    }

    @Transactional
    public void updateContent(Long id, String userId, String content) {
        Comment comment = findComment(id);
        validateUserIsWriter(comment, userId);
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(String userId, Long commentId) {
        Comment comment = findComment(commentId);
        validateUserIsWriter(comment, userId);
        comment.delete();
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private Comment findComment(Long parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    private void validateUserIsWriter(Comment comment, String userId) {
        if (!comment.isUserIsWriter(userId)) {
            throw new WriterDifferentException();
        }
    }
}