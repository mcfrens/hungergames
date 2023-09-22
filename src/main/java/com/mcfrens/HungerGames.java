package com.mcfrens;

import com.mcfrens.commands.*;
import com.mcfrens.listeners.PlayerDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HungerGames extends JavaPlugin {
    GameManager gameManager = new GameManager(this);

    @Override
    public void onEnable() {
        this.getCommand("joinhungergames").setExecutor(new JoinHungerGames(gameManager));
        this.getCommand("leavehungergames").setExecutor(new LeaveHungerGames(gameManager));
        this.getCommand("addstartlocation").setExecutor(new AddStartLocation(this));
        this.getCommand("resetstartlocations").setExecutor(new ResetStartLocations(this));
        this.getCommand("starthungergames").setExecutor(new StartHungerGames(gameManager));

        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        Bukkit.getLogger().info("Shutting Down");
    }

}
