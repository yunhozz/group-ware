package com.teamservice.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String leaderId;

    @Column(length = 30)
    private String name;

    private String imageUrl;

    public Team(String leaderId, String name, String imageUrl) {
        this.leaderId = leaderId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}