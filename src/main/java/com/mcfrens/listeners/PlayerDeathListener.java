package com.mcfrens.listeners;

import com.mcfrens.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    GameManager gameManager;

    public PlayerDeathListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(Component.text("You are in the game and eliminated."));
            Location deathLocation = player.getLocation();

            player.getWorld().strikeLightning(deathLocation);

            gameManager.eliminatePlayer(player);
        }
    }
}
