package com.mcfrens;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GameManager {
    public Boolean isInProgress = false;
    Plugin plugin;
    Location[] startLocations = new Location[0];
    Location[] chestLocations = new Location[0];
    Map<Player, Location> playerDeathLocations = new HashMap<>();
    ArrayList<Player> startingPlayers = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();
    Integer startDelay = 30;
    Integer timeTillStart = startDelay;

    public GameManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void joinGame(Player player) {
        if (!isInProgress) {
            startingPlayers.add(player);
        }
    }

    public void leaveGame(Player player) {
        startingPlayers.remove(player);
    }

    public void startGame(Player player) {
        try {
            setGameState();
            movePlayersIntoPosition();
            fillAllChests();
            startClock();
            Bukkit.getScheduler().runTaskLater(plugin, this::releasePlayers, startDelay * 20);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void fillAllChests() throws Exception {
        for (Location chestLocation : chestLocations) {
            prepareChest(chestLocation);
        }
    }

    public void eliminatePlayer(Player player) {
        activePlayers.remove(player);

        playerDeathLocations.put(player, player.getLocation());

        if (activePlayers.size() == 1) {
            Player lastPlayer = activePlayers.get(0);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title \"" + lastPlayer.getName() + " is the victor!\"");

            Bukkit.getScheduler().runTaskLater(plugin, this::endGame, 100L);
        } else if (activePlayers.isEmpty()) {
            endGame();
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say \"" + activePlayers.size() + " players remain.\"");
        }
    }

    public Boolean isPlayerInGame(Player player) {
        return activePlayers.contains(player);
    }

    public Location getDeathLocation(Player player) {
        return playerDeathLocations.get(player);
    }

    private void setGameState() throws Exception {
        timeTillStart = startDelay;
        isInProgress = true;
        activePlayers = startingPlayers;
        playerDeathLocations = new HashMap<>();
        startLocations = getLocations("locations.start");
        chestLocations = getLocations("locations.chests");
        clearTempBlocks();
    }

    private void movePlayersIntoPosition() {
        for (int i = 0; i < startingPlayers.size(); i++) {
            startingPlayers.get(i).teleport(startLocations[i]);
        }
    }

    private void startClock() {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title \"" + timeTillStart.toString() + "\"");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            timeTillStart -= 1;

            if (timeTillStart != 0) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title \"" + timeTillStart.toString() + "\"");
                startClock();
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title \"\"");
            }
        }, 20L);
    }

    private void releasePlayers() {
        for (Player player : startingPlayers) {
            Location location = player.getLocation();
            location.set(location.x(), location.y() + 3, location.z());
            player.teleport(location);

            Location tempBlockLocation = location.set(location.x(), location.y() - 2, location.z());

            tempBlockLocation.getBlock().setType(Material.GLASS);
        }
    }

    private void clearTempBlocks() {
        for (Location location : startLocations) {
            Location tempBlockLocation = location.set(location.x(), location.y() + 1, location.z());

            tempBlockLocation.getBlock().setType(Material.AIR);
        }
    }

    private void prepareChest(Location location) throws Exception {
        BlockState block;
        try {
            block = Objects.requireNonNull(plugin.getServer().getWorld("world")).getBlockAt(location).getState();
        } catch (NullPointerException e) {
            throw new Exception("Could not get block location for chest.");
        }

        if (block.getType() == Material.CHEST) {
            Chest chest = (Chest) block;

            clearChest(chest);
            prepareChest(chest);
        } else {
            throw new Exception("Saved chest location is not a chest!");
        }
    }

    private Location[] getLocations(String path) throws Exception {
        ArrayList<Location> loadedLocations = new ArrayList<>();

        List<String> locations = plugin.getConfig().getStringList(path);

        for (String location : locations) {
            try {
                String[] coordinates = location.split(",");
                double x = Double.parseDouble(coordinates[0]);
                double y = Double.parseDouble(coordinates[1]);
                double z = Double.parseDouble(coordinates[2]);

                Location worldLocation = new Location(plugin.getServer().getWorld("world"), x, y, z);

                loadedLocations.add(worldLocation);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

        Collections.shuffle(loadedLocations);

        return loadedLocations.toArray(Location[]::new);
    }

    private void clearChest(Chest chest) {
        chest.getBlockInventory().clear();
    }

    private void prepareChest(Chest chest) throws Exception {
        NamespacedKey key = NamespacedKey.fromString("hungergames:chests/hg_general");

        if (key != null) {
            LootTable lootTable = plugin.getServer().getLootTable(key);

            if (lootTable != null) {
                lootTable.fillInventory(chest.getInventory(), new Random(), new LootContext.Builder(chest.getLocation()).build());
            } else {
                throw new Exception("Could not load hunger games loot table. Did you add the MC Frens datapack?");
            }
        }
    }

    private void endGame() {
        for (Player player : startingPlayers) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setFlying(false);
            player.setHealth(0);
        }

        startingPlayers = new ArrayList<>();
        isInProgress = false;
    }
}
