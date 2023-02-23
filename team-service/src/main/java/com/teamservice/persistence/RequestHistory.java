package com.teamservice.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    private char status; // P: 진행중, Y: 수락

    private RequestHistory(String userId, Team team, char status) {
        this.userId = userId;
        this.team = team;
        this.status = status;
    }

    public static RequestHistory create(String userId, Team team) {
        return new RequestHistory(userId, team, 'P');
    }

    public void accept() {
        if (status == 'P') {
            status = 'Y';
        }
    }
}