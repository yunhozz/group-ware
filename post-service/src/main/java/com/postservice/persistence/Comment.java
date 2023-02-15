package com.postservice.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>(); // 대댓글

    @Builder
    private Comment(String writerId, Post post, String content, Comment parent, List<Comment> children) {
        this.writerId = writerId;
        this.post = post;
        this.content = content;
        this.parent = parent;
        this.children = children;
    }

    public static Comment create(String writerId, Post post, String content) {
        return Comment.builder()
                .writerId(writerId)
                .post(post)
                .content(content)
                .build();
    }

    public static Comment createChild(String writerId, Post post, String content, Comment parent) {
        Comment child = Comment.builder()
                .writerId(writerId)
                .post(post)
                .content(content)
                .parent(parent)
                .build();
        parent.addChild(child);
        return child;
    }

    private void addChild(Comment child) {
        children.add(child);
    }
}