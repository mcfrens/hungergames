package com.mcfrens.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ResetFireworkLocations implements CommandExecutor {
    Plugin plugin;

    public ResetFireworkLocations(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        plugin.getConfig().set("locations.fireworks", new ArrayList<String>());
        plugin.saveConfig();

        player.sendMessage(Component.text("Locations reset."));

        return true;
    }
}
