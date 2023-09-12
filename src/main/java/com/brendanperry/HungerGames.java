package com.brendanperry;

import com.brendanperry.commands.StartHungerGames;
import com.brendanperry.listeners.PlayerDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HungerGames extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("starthungergames").setExecutor(new StartHungerGames());

        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        Bukkit.getLogger().info("Shutting Down");
    }

}
