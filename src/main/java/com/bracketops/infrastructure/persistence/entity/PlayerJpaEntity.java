package com.bracketops.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class PlayerJpaEntity {

    @Id
    private String id;

    private String gamerTag;
    private String realName;
    private String inGameRole;

    public PlayerJpaEntity() {}

    public PlayerJpaEntity(String id, String gamerTag, String realName, String inGameRole) {
        this.id = id;
        this.gamerTag = gamerTag;
        this.realName = realName;
        this.inGameRole = inGameRole;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGamerTag() { return gamerTag; }
    public void setGamerTag(String gamerTag) { this.gamerTag = gamerTag; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getInGameRole() { return inGameRole; }
    public void setInGameRole(String inGameRole) { this.inGameRole = inGameRole; }
}
