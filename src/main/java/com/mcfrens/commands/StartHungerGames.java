package com.mcfrens.commands;

import com.mcfrens.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartHungerGames implements CommandExecutor {
    GameManager gameManager;

    public StartHungerGames(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (gameManager.isInProgress) {
            player.sendMessage(Component.text("Game is already in progress."));
            return false;
        }

        player.sendMessage(Component.text("Starting Hunger Games."));

        gameManager.startGame(player);

        return true;
    }
}
