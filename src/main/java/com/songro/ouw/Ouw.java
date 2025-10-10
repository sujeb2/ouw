package com.songro.ouw;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import com.songro.ouw.command.SetupDuel;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class Ouw extends JavaPlugin {

    public static Logger log = PaperPluginLogger.getLogger("Ouw");
    public static Ouw plugin;

    public File config_file;
    private FileConfiguration config_data;

    @Override
    public void onEnable() {
        plugin = this;
        try {
            log.info("Starting up");
            initConfig();

            Objects.requireNonNull(getCommand("setup")).setExecutor(new SetupDuel());
        } catch (Exception e) {
            log.severe("Something really bad happened while running initial startup.");
            log.severe(e.getLocalizedMessage());
        }
    }

    @Override
    public void onDisable() {
        log.info("Shutdown");
    }

    public void initConfig() {
        config_file = new File(getDataFolder(), "pldata.yml");
        if (!config_file.exists()) {
            log.warning("Initializing data.");
            config_file.getParentFile().mkdirs();
            saveResource("config.yml", false);
            log.info("Initialized");
        } else {
            log.info("Found.");
        }

        config_data = new YamlConfiguration();
        try {
            config_data.load(config_file);
        } catch (IOException | InvalidConfigurationException e) {
            log.severe("Failed to load config! Is File valid?");
            plugin.setEnabled(false);
        }
    }

    public FileConfiguration getConfigData() {
        return config_data;
    }
}
