package com.songro.ouw;

import org.bukkit.entity.Player;

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

}
