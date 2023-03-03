package com.mailservice.persistence.entity.mail;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("SM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpamMail extends Mail {

    public SpamMail(String writerId, String title, String content) {
        super(title, content, false, false);
        UserMail userMail = new UserMail(writerId, this);
        addUserMail(userMail);
    }
}