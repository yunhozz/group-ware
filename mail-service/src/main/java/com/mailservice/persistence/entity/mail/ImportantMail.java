package com.mailservice.persistence.entity.mail;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("IM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImportantMail extends Mail {

    private boolean hasRemind;

    private LocalDateTime remindTime;

    public ImportantMail(String writerId, String title, String content) {
        super(title, content, false, false);
        this.hasRemind = false;
        this.remindTime = null;
        UserMail userMail = new UserMail(writerId, this);
        addUserMail(userMail);
    }

    public void updateRemind(LocalDateTime remindTime) {
        this.hasRemind = true;
        this.remindTime = remindTime;
    }
}