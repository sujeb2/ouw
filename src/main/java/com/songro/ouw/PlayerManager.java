package com.songro.ouw;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager {

    static Player p;

    public PlayerManager(Player player) {
        p = player;
    }

    public boolean getDuelStatus() {
        return Ouw.plugin.getConfigData().getBoolean(p.getUniqueId() + ".isOnDuel");
    }

    public void setDuelStatus(boolean status) {
        Ouw.plugin.getConfigData().set(p.getUniqueId() + ".isOnDuel", status);
    }

    public String getMatchWorld() {
        return Ouw.plugin.getConfigData().getString(p.getUniqueId() + ".currentMap");
    }

    public void initPlayerData() {
        if(!Ouw.plugin.getConfigData().contains(p.getUniqueId().toString())) {
            Ouw.log.info("Initializing player data for " + p.getName());

        } else {
            Ouw.log.warning("Player data has already initialized.");
        }
    }
}
