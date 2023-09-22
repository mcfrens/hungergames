package com.mcfrens.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddStartLocation implements CommandExecutor {
    Plugin plugin;

    public AddStartLocation(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        List<String> startLocations = plugin.getConfig().getStringList("locations.start");
        startLocations.add(String.format("%f,%d,%f", location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5));
        plugin.getConfig().set("locations.start", startLocations);
        plugin.saveConfig();

        player.sendMessage(Component.text("Location added. There are now " + startLocations.size() + " start locations."));

        return true;
    }
}
