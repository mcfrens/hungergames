package com.mcfrens.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SetFinalBorderSize implements CommandExecutor {
    private final Plugin plugin;

    public SetFinalBorderSize(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /setfinalbordersize <size>");
            return true;
        }

        double size;
        try {
            size = Double.parseDouble(args[0]);
            if (size <= 1.0) {
                sender.sendMessage("Final border size must be greater than 1.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number.");
            return true;
        }

        plugin.getConfig().set("settings.finalBorderSize", size);
        plugin.saveConfig();

        sender.sendMessage("Hunger Games final border size set to " + size + " blocks.");
        return true;
    }
}
