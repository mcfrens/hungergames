package com.mcfrens.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class StartHungerGames implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player playerWhoTriggeredCommand = (Player) sender;
        String worldName = playerWhoTriggeredCommand.getLocation().getWorld().getName();

        ArrayList<Location> startLocations = new ArrayList<>();
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));
        startLocations.add(new Location(Bukkit.getWorld(worldName), -2948, 79, -118, 0, 0));

        // Shuffle the locations so people spawn in a random one each time
        Collections.shuffle(startLocations);

        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        if (players.length < 2) {
            playerWhoTriggeredCommand.sendMessage("The game requires at least 2 players.");
            return false;
        }

        for (int i = 0; i < players.length; i++) {
            players[i].teleport(startLocations.get(i));
        }

        return true;
    }
}
