package com.mailservice.persistence.entity.mail;

import com.mailservice.persistence.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = false")
public abstract class Mail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 2000)
    private String content;

    private boolean isRed;

    private boolean isDeleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mail", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<UserMail> userMailList = new ArrayList<>();

    @Column(insertable = false, updatable = false)
    private String dtype; // for querydsl

    public Mail(String title, String content, boolean isRed, boolean isDeleted) {
        this.title = title;
        this.content = content;
        this.isRed = isRed;
        this.isDeleted = isDeleted;
    }

    public void read() {
        if (!isRed) {
            isRed = true;
        }
    }

    public void delete() {
        if (!isDeleted) {
            isDeleted = true;
        } else throw new IllegalStateException("이미 삭제된 메일입니다.");
    }

    protected void addUserMail(UserMail userMail) {
        this.userMailList.add(userMail);
        userMail.setMail(this);
    }
}