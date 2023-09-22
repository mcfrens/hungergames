package com.mcfrens.commands;

import com.mcfrens.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinHungerGames implements CommandExecutor {
    GameManager gameManager;

    public JoinHungerGames(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        gameManager.joinGame(player);

        player.sendMessage(Component.text("You have joined Hunger Games."));

        return true;
    }
}
