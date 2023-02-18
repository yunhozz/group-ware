package com.postservice.persistence;

import com.postservice.common.enums.PostType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 'N'")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writerId;

    private Long teamId;

    private String title;

    @Column(length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType postType; // MUST_READ, NOTICE, REPORT

    private int view;

    private char isDeleted; // Y, N

    @Builder
    private Post(String writerId, Long teamId, String title, String content, PostType postType, int view, char isDeleted) {
        this.writerId = writerId;
        this.teamId = teamId;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.view = view;
        this.isDeleted = isDeleted;
    }

    public static Post create(String writerId, Long teamId, String title, String content, PostType postType) {
        return Post.builder()
                .writerId(writerId)
                .teamId(teamId)
                .title(title)
                .content(content)
                .postType(postType)
                .view(0)
                .isDeleted('N')
                .build();
    }

    public boolean isUserIsWriter(String userId) {
        return writerId.equals(userId);
    }

    public void updateInfo(String title, String content, PostType postType) {
        this.title = title;
        this.content = content;
        this.postType = postType;
    }

    public void addView() {
        view += 1;
    }

    public void delete() {
        if (isDeleted == 'N') {
            isDeleted = 'Y';
        } else throw new IllegalStateException("이미 삭제된 게시물입니다.");
    }
}