package com.mailservice.persistence.entity;

import com.mailservice.common.enums.SecurityRating;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecuritySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SecurityRating rating; // GENERAL, CONFIDENTIAL

    private LocalDateTime validity; // 하루, 일주일, 한달

    public SecuritySetting(SecurityRating rating, LocalDateTime validity) {
        this.rating = rating;
        this.validity = validity;
    }
}