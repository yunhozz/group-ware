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

}