package com.mcfrens.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SetStartingBorderSize implements CommandExecutor {
    private final Plugin plugin;

    public SetStartingBorderSize(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /setstartingborder <size>");
            return true;
        }

        int size;
        try {
            size = Integer.parseInt(args[0]);
            if (size <= 0) {
                sender.sendMessage("Border size must be a positive number.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number.");
            return true;
        }

        plugin.getConfig().set("settings.startingBorderSize", size);
        plugin.saveConfig();

        sender.sendMessage("Hunger Games border size set to " + size + ".");
        return true;
    }
}