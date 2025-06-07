package com.mcfrens;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    public Boolean isInProgress = false;
    Plugin plugin;
    Location[] startLocations = new Location[0];
    Location[] chestLocations = new Location[0];
    Map<Player, Location> playerDeathLocations = new HashMap<>();
    ArrayList<HungerGamesPlayer> players = new ArrayList<>();
    Integer startDelay = 10;
    Integer timeTillStart = startDelay;

    public GameManager(Plugin plugin) {
        this.plugin = plugin;

        try {
            updateConfigs();
        } catch(Exception ex) {
            System.out.println("[GameManager] Could not find locations.start");
        }
    }

    public void startGame(Player player) {
        if (chestLocations.length == 0) {
            player.sendMessage("You have no chests.");
            return;
        }

        setStartingPlayers();

        if (startLocations.length < players.size()) {
            player.sendMessage("There are not enough spawn points for all the players. Start Locations: " + startLocations.length + " Starting Players: " + players.size());
            return;
        }

        try {
            updateConfigs();
            setGameState();
            movePlayersIntoPosition();
            fillAllChests();
            startClock();
            Bukkit.getScheduler().runTaskLater(plugin, this::releasePlayers, (startDelay + 1) * 20L);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void updateConfigs() throws Exception {
        startLocations = getLocations("locations.start");
        chestLocations = getLocations("locations.chests");
    }

    private void fillAllChests() throws Exception {
        for (Location chestLocation : chestLocations) {
            if (chestLocation != null) {
                prepareChest(chestLocation);
            }
        }
    }

    public void eliminatePlayer(Player player) {
        if (isInProgress == false) { return; }

        deactivatePlayer(player);

        List<HungerGamesPlayer> activePlayers = getActivePlayers();

        playerDeathLocations.put(player, player.getLocation());

        if (activePlayers.size() == 1) {
            Player lastPlayer = activePlayers.get(0).player;
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title \"" + lastPlayer.getName() + " is the victor!\"");

            Bukkit.getScheduler().runTaskLater(plugin, this::endGame, 100L);
        } else if (activePlayers.isEmpty()) {
            endGame();
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say \"" + activePlayers.size() + " players remain.\"");
        }
    }

    public Boolean isPlayerInGame(Player player) {
        for (HungerGamesPlayer hgPlayer : players) {
            if (hgPlayer.player.equals(player) && hgPlayer.isActive) {
                return true;
            }
        }

        return false;
    }

    public Location getDeathLocation(Player player) {
        return playerDeathLocations.get(player);
    }

    public void endGame() {
        isInProgress = false;
        resetWorldBorder();

        for (HungerGamesPlayer hgPlayer : players) {
            Player player = hgPlayer.player;
            player.setGameMode(GameMode.ADVENTURE);
            player.setFlying(false);
            player.setHealth(0);
        }

        players.clear();
        clearDroppedItems();

        try {
            launchFireworks();
        } catch (Exception e) {
            System.out.println("Ooof fireworks fail");
        }
    }

    private void setStartingPlayers() {
        ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        players = onlinePlayers.stream()
                .map(HungerGamesPlayer::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void setGameState() {
        resetWorldBorder();
        timeTillStart = startDelay;
        isInProgress = true;
        activateAllPlayers();
        playerDeathLocations = new HashMap<>();
        clearTempBlocks();
        setRandomTime();
    }

    private void movePlayersIntoPosition() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i).player;
            player.teleport(startLocations[i]);
            player.setGameMode(GameMode.ADVENTURE);
            player.setFlying(false);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            try {
                launchFireworks();
            } catch (Exception e) {
                System.out.println("Ooof fireworks fail");
            }
        }
    }

    private void startClock() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String title;

            if (timeTillStart != 0) {
                title = timeTillStart.toString();
                startClock();
            } else {
                title = "May the odds be ever in your favor.";
                startBorderShrink();
            }

            for (HungerGamesPlayer hgPlayer : players) {
                hgPlayer.player.sendTitle("", title);
            }

            timeTillStart -= 1;
        }, 20L);
    }

    // set the center of the border on the map itself
    private void resetWorldBorder() {
        // would need to edit this if the game needed to work in a different world
        World world = Bukkit.getServer().getWorld("world");
        if (world != null) {
            WorldBorder border = world.getWorldBorder();
            int borderSize = plugin.getConfig().getInt("settings.startingBorderSize", 350);
            border.setSize(borderSize);
        }
    }

    private void startBorderShrink() {
        // would need to edit this if the game needed to work in a different world
        World world = Bukkit.getServer().getWorld("world");
        if (world != null) {
            WorldBorder border = world.getWorldBorder();
            int shrinkTime = plugin.getConfig().getInt("settings.borderShrinkTime", 900); // 15 minutes default
            double finalSize = plugin.getConfig().getDouble("settings.finalBorderSize", 25.0);
            border.setSize(finalSize, shrinkTime);
        }
    }

    private void releasePlayers() {
        for (HungerGamesPlayer hgPlayer : players) {
            Player player = hgPlayer.player;
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

            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block;

                clearChest(chest);
                prepareChest(chest);
            } else {
                System.out.println("Could not get block location for chest.");
            }
        } catch (NullPointerException e) {
            System.out.println("Could not get block location for chest.");
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

    private void clearChest(Chest chest)
    {
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

    private void setRandomTime() {
        World world = Bukkit.getWorlds().get(0); // Get the first world

        Random random = new Random();
        long randomTime = random.nextInt(24000);

        world.setTime(randomTime);
    }

    private List<HungerGamesPlayer> getActivePlayers() {
        return players.stream()
                .filter(HungerGamesPlayer::isActive) // Filter based on active status
                .toList();
    }

    private void deactivatePlayer(Player player) {
        players.stream()
                .filter(p -> p.player.equals(player))
                .findFirst()
                .ifPresent(p -> p.setActive(false)); // Mark as inactive
    }

    private void activateAllPlayers() {
        players.forEach(player -> player.setActive(true));
    }

    private void clearDroppedItems() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.getEntitiesByClass(org.bukkit.entity.Item.class).forEach(Entity::remove);
        }
    }
}
