package com.teamservice.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamUser> teamUserList = new ArrayList<>();

    private Team(String leaderId, String name, String imageUrl) {
        this.leaderId = leaderId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static Team create(String leaderId, String name, String imageUrl) {
        Team team = new Team(leaderId, name, imageUrl);
        TeamUser teamUser = new TeamUser(team, leaderId);
        team.addTeamUser(teamUser);

        return team;
    }

    public boolean isCreatedLowerThanThreeDaysBefore() {
        LocalDateTime threeDaysBefore = LocalDateTime.now().minusDays(3);
        return getCreatedAt().isAfter(threeDaysBefore);
    }

    public boolean isModifiedLowerThanOneDayBefore() {
        LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1);
        return getModifiedAt().isAfter(oneDayBefore);
    }

    public boolean isLeader(String userId) {
        return leaderId.equals(userId);
    }

    public void updateInfo(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void changeLeader(String userId) {
        leaderId = userId;
    }

    private void addTeamUser(TeamUser teamUser) {
        teamUserList.add(teamUser);
        teamUser.setTeam(this);
    }
}