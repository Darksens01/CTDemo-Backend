package com.bracketops.domain.model.entity;

import java.util.Objects;
import java.util.UUID;

public class Player {
    private final String id;
    private final String gamerTag;
    private final String realName;
    private final String inGameRole; // e.g. "Sniper", "Entry Fragger", "Mid Laner"

    public Player(String id, String gamerTag, String realName, String inGameRole) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.gamerTag = gamerTag;
        this.realName = realName;
        this.inGameRole = inGameRole != null ? inGameRole : "Player";
    }

    public String getId() { return id; }
    public String getGamerTag() { return gamerTag; }
    public String getRealName() { return realName; }
    public String getInGameRole() { return inGameRole; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
