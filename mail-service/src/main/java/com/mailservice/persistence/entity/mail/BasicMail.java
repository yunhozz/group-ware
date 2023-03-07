package com.mailservice.persistence.entity.mail;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("BM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicMail extends Mail {

    public BasicMail(String writerId, String title, String content) {
        super(title, content, false, false);
        UserMail userMail = new UserMail(writerId);
        addUserMail(userMail);
    }
}