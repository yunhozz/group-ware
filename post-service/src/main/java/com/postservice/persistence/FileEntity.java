package com.postservice.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String fileId;

    private String originalName;

    private String saveName;

    private String savePath;

    @Builder
    private FileEntity(Post post, String fileId, String originalName, String saveName, String savePath) {
        this.post = post;
        this.fileId = fileId;
        this.originalName = originalName;
        this.saveName = saveName;
        this.savePath = savePath;
    }
}