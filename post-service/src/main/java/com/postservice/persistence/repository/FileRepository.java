package com.postservice.persistence.repository;

import com.postservice.persistence.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("select f.id from FileEntity f join f.post p where p.id = :postId")
    List<Long> findIdsByPostId(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("delete FileEntity f where f.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    default void deleteFilesByPostId(Long postId) {
        List<Long> ids = findIdsByPostId(postId);
        deleteAllByIds(ids);
    }
}