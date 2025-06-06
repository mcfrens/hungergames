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
        if (gameManager.isInProgress == false) {
            return;
        }

        Player player = event.getPlayer();

        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(Component.text("You have been eliminated."));
            Location deathLocation = player.getLocation();
            deathLocation.add(5, 20, 5);

            player.getWorld().strikeLightning(deathLocation);

            gameManager.eliminatePlayer(player);
        }
    }
}
