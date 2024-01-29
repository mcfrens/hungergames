package com.mcfrens.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AddChestLocation implements CommandExecutor {
    Plugin plugin;

    public AddChestLocation(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        List<String> chestLocations = plugin.getConfig().getStringList("locations.chests");

        String newLocation = String.format("%d,%d,%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (chestLocations.contains(newLocation)) {
            player.sendMessage("This location was already added.");
            return false;
        } else {
            Block block = Objects.requireNonNull(plugin.getServer().getWorld("world")).getBlockAt(location);

            if (block.getType() != Material.CHEST) {
                player.sendMessage("This is not a chest!");
                return false;
            }
        }

        chestLocations.add(newLocation);
        plugin.getConfig().set("locations.chests", chestLocations);
        plugin.saveConfig();

        player.sendMessage(Component.text("Location added. There are now " + chestLocations.size() + " chest locations."));

        return true;
    }
}
