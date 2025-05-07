package com.mcfrens.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFireworkLocation implements CommandExecutor {
    Plugin plugin;

    public AddFireworkLocation(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        List<String> fireworkLocations = plugin.getConfig().getStringList("locations.fireworks");

        String newLocation = String.format("%d,%d,%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());

        fireworkLocations.add(newLocation);
        plugin.getConfig().set("locations.fireworks", fireworkLocations);
        plugin.saveConfig();

        player.sendMessage(Component.text("Location added. There are now " + fireworkLocations.size() + " firework locations."));

        return true;
    }
}
