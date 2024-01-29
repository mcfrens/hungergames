package com.mcfrens.listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.mcfrens.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerRespawnListener implements Listener {
    GameManager gameManager;

    public PlayerRespawnListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        if (gameManager.isInProgress) {
            Player player = event.getPlayer();
            Location deathLocation = gameManager.getDeathLocation(player);

            player.setGameMode(GameMode.SPECTATOR);
            player.setFlying(true);

            player.teleport(deathLocation);
        }
    }
}
