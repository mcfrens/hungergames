package com.mcfrens;

import com.mcfrens.commands.*;
import com.mcfrens.listeners.PlayerDeathListener;
import com.mcfrens.listeners.PlayerLeaveListener;
import com.mcfrens.listeners.PlayerRespawnListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/*
TODO:
- Block players from joining once game has started
- Determine when game is over
 */

public final class HungerGames extends JavaPlugin {
    GameManager gameManager = new GameManager(this);

    @Override
    public void onEnable() {
        this.getCommand("joinhungergames").setExecutor(new JoinHungerGames(gameManager));
        this.getCommand("leavehungergames").setExecutor(new LeaveHungerGames(gameManager));
        this.getCommand("addstartlocation").setExecutor(new AddStartLocation(this));
        this.getCommand("resetstartlocations").setExecutor(new ResetStartLocations(this));
        this.getCommand("starthungergames").setExecutor(new StartHungerGames(gameManager));
        this.getCommand("addchestlocation").setExecutor(new AddChestLocation(this));
        this.getCommand("addallchestlocations").setExecutor(new AddAllChestLocations(this));
        this.getCommand("resetchestlocations").setExecutor(new ResetChestLocations(this));
        this.getCommand("endhungergames").setExecutor(new EndHungerGames(gameManager));
        this.getCommand("addfireworklocation").setExecutor(new AddFireworkLocation(this));
        this.getCommand("resetfireworklocations").setExecutor(new ResetFireworkLocations(this));

        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(gameManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(gameManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(gameManager), this);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        Bukkit.getLogger().info("Shutting Down");
    }

}
