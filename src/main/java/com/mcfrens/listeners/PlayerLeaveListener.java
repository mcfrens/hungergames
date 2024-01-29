package com.mcfrens.listeners;

import com.mcfrens.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    GameManager gameManager;

    public PlayerLeaveListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        gameManager.eliminatePlayer(event.getPlayer());
    }
}
