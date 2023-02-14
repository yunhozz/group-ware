package com.postservice.persistence.repository;

import com.postservice.persistence.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    boolean existsByIdAndWriterId(Long id, String userId);
}