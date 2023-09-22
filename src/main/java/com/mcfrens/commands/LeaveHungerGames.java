package com.mcfrens.commands;

import com.mcfrens.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveHungerGames implements CommandExecutor {
    GameManager gameManager;

    public LeaveHungerGames(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        gameManager.leaveGame(player);

        player.sendMessage(Component.text("You have left Hunger Games."));

        return true;
    }
}
