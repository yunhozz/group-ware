package com.postservice.persistence;

import jakarta.persistence.Column;
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

    @Column(length = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Comment> children = new ArrayList<>(); // 대댓글

    private char isDeleted; // Y, N

    @Builder
    private Comment(String writerId, Post post, String content, Comment parent, List<Comment> children, char isDeleted) {
        this.writerId = writerId;
        this.post = post;
        this.content = content;
        this.parent = parent;
        this.children = children;
        this.isDeleted = isDeleted;
    }

    public static Comment create(String writerId, Post post, String content) {
        return Comment.builder()
                .writerId(writerId)
                .post(post)
                .content(content)
                .isDeleted('N')
                .build();
    }

    public static Comment createChild(String writerId, Post post, String content, Comment parent) {
        if (parent.isDeleted == 'Y') {
            throw new IllegalStateException("이미 삭제된 댓글입니다.");
        }

        Comment child = Comment.builder()
                .writerId(writerId)
                .post(post)
                .content(content)
                .parent(parent)
                .isDeleted('N')
                .build();
        parent.addChild(child);
        return child;
    }

    public boolean isUserIsWriter(String userId) {
        return writerId.equals(userId);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        if (isDeleted == 'N') {
            content = "[삭제된 댓글입니다]";
            isDeleted = 'Y';
        } else throw new IllegalStateException("이미 삭제된 댓글입니다.");
    }

    private void addChild(Comment child) {
        children.add(child);
    }
}