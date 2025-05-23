package com.mcfrens;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
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
    Integer startDelay = 10;
    Integer timeTillStart = startDelay;

    public GameManager(Plugin plugin) {
        this.plugin = plugin;

        try {
            startLocations = getLocations("locations.start");
            chestLocations = getLocations("locations.chests");
        } catch(Exception ex) {
            System.out.println("[GameManager] Could not find locations.start");
        }
    }

    public void joinGame(Player player) {
        if (isInProgress) {
            player.sendMessage("The game has already started.");
        } else if (startingPlayers.contains(player)) {
            player.sendMessage("You are already in the game.");
        } else {
            startingPlayers.add(player);
            player.sendMessage("You have joined the game.");
        }
    }

    public void leaveGame(Player player) {
        startingPlayers.remove(player);
    }

    public void startGame(Player player) {
        if (chestLocations.length == 0) {
            player.sendMessage("You have no chests.");
            return;
        }

        if (startLocations.length < startingPlayers.size()) {
            player.sendMessage("There are not enough spawn points for all the players.");
            return;
        }

        try {
            setGameState();
            movePlayersIntoPosition();
            fillAllChests(player);
            startClock();
            Bukkit.getScheduler().runTaskLater(plugin, this::releasePlayers, (startDelay + 1) * 20L);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void fillAllChests(Player player) throws Exception {
        for (Location chestLocation : chestLocations) {
            prepareChest(chestLocation, player);
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

    public void endGame() {
        for (Player player : startingPlayers) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setFlying(false);
            player.setHealth(0);
        }

        startingPlayers = new ArrayList<>();
        isInProgress = false;

        try {
            launchFireworks();
        } catch (Exception e) {
            System.out.println("Ooof fireworks fail");
        }
    }

    private void setGameState() {
        timeTillStart = startDelay;
        isInProgress = true;
        activePlayers = startingPlayers;
        playerDeathLocations = new HashMap<>();
        clearTempBlocks();
    }

    private void movePlayersIntoPosition() {
        for (int i = 0; i < startingPlayers.size(); i++) {
            Player player = startingPlayers.get(i);
            player.teleport(startLocations[i]);
            player.setGameMode(GameMode.ADVENTURE);
            player.setFlying(false);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
        }
    }

    private void startClock() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String title;

            if (timeTillStart != 0) {
                title = timeTillStart.toString();
                startClock();
                try {
                    launchFireworks();
                } catch (Exception e) {
                    System.out.println("Ooof fireworks fail");
                }
            } else {
                title = "May the odds be ever in your favor.";
            }

            for (Player player : activePlayers) {
                player.sendTitle("", title);
            }

            timeTillStart -= 1;
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

    private void prepareChest(Location location, Player player) throws Exception {
        BlockState block;
        try {
            block = Objects.requireNonNull(plugin.getServer().getWorld("world")).getBlockAt(location).getState();
        } catch (NullPointerException e) {
            throw new Exception("Could not get block location for chest.");
        }

        if (block.getType() == Material.CHEST) {
            Chest chest = (Chest) block;

            clearChest(chest, player);
            prepareChest(chest, player);
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

    private void clearChest(Chest chest, Player player)
    {
        chest.getBlockInventory().clear();
    }

    private void prepareChest(Chest chest, Player player) throws Exception {
        NamespacedKey key = NamespacedKey.fromString("hungergames:chests/hg_general");

        if (key != null) {
            LootTable lootTable = plugin.getServer().getLootTable(key);

            if (lootTable != null) {
                lootTable.fillInventory(chest.getInventory(), new Random(), new LootContext.Builder(chest.getLocation()).build());
            } else {
                throw new Exception("Could not load hunger games loot table. Did you add the MC Frens datapack?");
            }
        } else {
            throw new Exception("Could not load hunger games loot table. Did you add the MC Frens datapack?");
        }
    }

    private void launchFireworks() throws Exception {
        Location[] locations = getLocations("locations.fireworks");

        for (Location location : locations) {
            launchFirework(location);
        }
    }

    private void launchFirework(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        // Customize firework effect
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED)
                .withFade(Color.YELLOW)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build();

        meta.addEffect(effect);
        meta.setPower(1); // Flight duration
        firework.setFireworkMeta(meta);
    }
}
