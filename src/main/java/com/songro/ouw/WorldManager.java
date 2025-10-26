package com.songro.ouw;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions;
import org.mvplugins.multiverse.core.world.options.RemoveWorldOptions;
import org.mvplugins.multiverse.external.jvnet.hk2.annotations.Optional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class WorldManager {

    MultiverseCoreApi core;
    org.mvplugins.multiverse.core.world.WorldManager worldManager;
    Logger log;

    public WorldManager() {
        core = MultiverseCoreApi.get();
        worldManager = core.getWorldManager();
        log = Ouw.log;
    }

    public CompletableFuture<Boolean> generateTempMap(@NotNull String templateWorldName, @NotNull final String UUID) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(templateWorldName.isEmpty() || !Ouw.plugin.getConfigData().contains("worlds." + templateWorldName)) {
            log.severe("Invalid world name or, un-initialized world name has been checked.");
            log.warning("Please initialize world before startup duel match.");
            future.complete(false);
            return future;
        }
        if(UUID.isBlank()) {
            log.severe("Invalid UUID has been found.");
            log.warning("Stopping cloning process..");
            future.complete(false);
            return future;
        }

        final String newWorldName = templateWorldName + "_" + UUID;
        boolean keepGameRule = Ouw.plugin.getConfigData().getBoolean("worlds." + templateWorldName + ".copyWorldGameRule");
        boolean keepWorldBorder = Ouw.plugin.getConfigData().getBoolean("worlds." + templateWorldName + ".keepWorldBorder");
        boolean copyDefaultSpawn = Ouw.plugin.getConfigData().getBoolean("worlds." + templateWorldName + ".copyDefaultSpawn");

        worldManager.getWorld(templateWorldName).peek(templateWorld -> {
           log.info("Start cloning process..");
            worldManager.cloneWorld(CloneWorldOptions.fromTo((LoadedMultiverseWorld) templateWorld, newWorldName)
                            .keepGameRule(keepGameRule)
                            .keepWorldBorder(keepWorldBorder))
                    .onSuccess(clonedWorld -> {
                        log.info("World cloned successfully.");
                        clonedWorld.setSpawnLocation(copyDefaultSpawn ? templateWorld.getSpawnLocation() : Ouw.plugin.getConfigData().getObject("worlds."+templateWorldName+".player1_spawn", Location.class));
                        future.complete(true);
                        log.info("Done.");
                    })
                    .onFailure(reason -> {
                        log.severe("Failed to clone world '" + templateWorldName + "'. Reason: " + reason);
                        future.complete(false);
                    });
        }).onEmpty(() -> {
            log.severe("Template world '" + templateWorldName + "' not found.");
            future.complete(false);
        });
        
        return future;
    }

    public CompletableFuture<Boolean> removeWorld(@NotNull String worldName, @NotNull Boolean force_remove) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(!force_remove && Bukkit.getWorld(worldName).getPlayerCount() > 0) {
            log.warning("Cannot remove world because players are still logged in that world!");
            future.complete(false);
            return future;
        }

        worldManager.getWorld(worldName).peek(world -> {
           worldManager.removeWorld(RemoveWorldOptions.world(world))
                   .onSuccess(success -> {
                       log.info("Successfully removed world: " + world.getName());
                       future.complete(true);
                   })
                   .onFailure(failed -> {
                       log.severe("Failed to remove world: " + world.getName());
                       log.warning("Reason: " + failed);
                       future.complete(false);
                   });
        }).onEmpty(() -> {
            log.warning("Invalid world name.");
            future.complete(false);
        });

        return future;
    }

    public CompletableFuture<Boolean> teleport(@NotNull Player p, @Optional List<Player> pList) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(!p.isOnline()) {
            log.severe("Invalid Player profile.");
            future.complete(false);
            return future;
        }

        return future;
    }

}
