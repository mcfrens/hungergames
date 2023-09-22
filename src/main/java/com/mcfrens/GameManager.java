package com.mcfrens;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    Plugin plugin;
    ArrayList<Location> startLocations = new ArrayList<>();
    ArrayList<Location> chestLocations = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();

    Boolean isInProgress = false;

    public GameManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void joinGame(Player player) {
//        if (!isInProgress) {
        players.add(player);
//        }
    }

    public void leaveGame(Player player) {
        players.remove(player);
    }

    public void startGame() {
        isInProgress = true;

        loadStartLocations();

        for (int i = 0; i < players.size(); i++) {
            players.get(i).teleport(startLocations.get(i));
        }
    }

    private void loadStartLocations() {
        this.startLocations.clear();

        List<String> startLocations = plugin.getConfig().getStringList("locations.start");

        for (String startLocation : startLocations) {
            String[] coordinates = startLocation.split(",");
            double x = Double.parseDouble(coordinates[0]);
            double y = Double.parseDouble(coordinates[1]);
            double z = Double.parseDouble(coordinates[2]);

            Location location = new Location(plugin.getServer().getWorld("world"), x, y, z);

            this.startLocations.add(location);
        }

        Collections.shuffle(this.startLocations);
    }
}
