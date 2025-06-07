package com.mcfrens;

import org.bukkit.entity.Player;

public class HungerGamesPlayer {
    public Player player;
    public Boolean isActive;

    public HungerGamesPlayer(Player player) {
        this.player = player;
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}