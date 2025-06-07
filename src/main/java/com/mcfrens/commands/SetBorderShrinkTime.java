package com.mcfrens.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SetBorderShrinkTime implements CommandExecutor {
    private final Plugin plugin;

    public SetBorderShrinkTime(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /setbordershrinktime <seconds>");
            return true;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(args[0]);
            if (seconds <= 0) {
                sender.sendMessage("Shrink time must be a positive number.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number.");
            return true;
        }

        plugin.getConfig().set("settings.borderShrinkTime", seconds);
        plugin.saveConfig();

        sender.sendMessage("Hunger Games border shrink time set to " + seconds + " seconds.");
        return true;
    }
}
