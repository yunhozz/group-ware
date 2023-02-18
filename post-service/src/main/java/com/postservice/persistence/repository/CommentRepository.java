package com.postservice.persistence.repository;

import com.postservice.persistence.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c.id from Comment c join c.post p where p.id = :postId")
    List<Long> findIdsByPostId(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update Comment c set c.isDeleted = cast('Y' as char) where c.id in :ids")
    void deleteInIds(@Param("ids") List<Long> ids);

    default void deleteCommentsByPostId(Long postId) {
        List<Long> ids = findIdsByPostId(postId);
        deleteInIds(ids);
    }
}