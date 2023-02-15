package com.postservice.persistence.repository;

import com.postservice.persistence.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}