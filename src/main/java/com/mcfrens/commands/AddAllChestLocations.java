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

public class AddAllChestLocations implements CommandExecutor {
    Plugin plugin;

    public AddAllChestLocations(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        List<String> chestLocations = plugin.getConfig().getStringList("locations.chests");

        List<Chest> chests = new ArrayList<>();

        for (Chunk chunk : player.getWorld().getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Chest chest) {
                    chests.add(chest);
                }
            }
        }

        player.sendMessage("Found " + chests.size() + " chest(s).");

        for (Chest chest : chests) {
            Location location = chest.getLocation();
            String newLocation = String.format("%d,%d,%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());

            if (chestLocations.contains(newLocation)) {
                continue;
            } else {
                Block block = Objects.requireNonNull(plugin.getServer().getWorld("world")).getBlockAt(location);

                if (block.getType() != Material.CHEST) {
                    continue;
                }
            }

            chestLocations.add(newLocation);
        }

        plugin.getConfig().set("locations.chests", chestLocations);
        plugin.saveConfig();

        player.sendMessage(Component.text("Locations added. There are now " + chestLocations.size() + " chest locations."));

        return true;
    }
}
