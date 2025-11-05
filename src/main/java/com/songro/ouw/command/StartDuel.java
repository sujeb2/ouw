package com.songro.ouw.command;

import com.songro.ouw.Ouw;
import com.songro.ouw.WorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StartDuel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player p = (Player) commandSender;
        if(args.length > 5) {
            p.sendMessage(Component.text("Invalid command usage").color(NamedTextColor.YELLOW));
            return false;
        }

        try {
            boolean isRandomWorld = Boolean.parseBoolean(args[4]);
            Player target_1 = Bukkit.getPlayer(args[0]);
            Player target_2 = Bukkit.getPlayer(args[1]);
            List<Player> players = List.of(target_1, target_2);
            String worldName = args[2];
            WorldManager worldManager = new WorldManager();
            String uuid = UUID.randomUUID().toString().substring(0, 8);

            CompletableFuture<Boolean> checkWorldGenerated = worldManager.generateTempMap(worldName, uuid);
            if (!checkWorldGenerated.get()) {
                p.sendMessage(Component.text("Failed to generate map due to unknown error, please report this error to developer.").color(NamedTextColor.RED));
                return true;
            }
            Ouw.plugin.getLogger().info("World generated, starting match.");
            CompletableFuture<Boolean> teleportPlayer = worldManager.teleport(target_1, players, worldName+uuid);
            if(!teleportPlayer.get()) {
                p.sendMessage(Component.text("Invalid player teleport error, please report this error to developer.").color(NamedTextColor.RED));
                CompletableFuture<Boolean> removeWorld = worldManager.removeWorld(worldName+uuid, true);
                if(!removeWorld.get()) {
                    p.sendMessage(Component.text("Failed to remove world, is world being accessed by another process?").color(NamedTextColor.RED));
                    return true;
                }
                /**
                 for(Player p2 : players) { // todo: need to teleport player to back to original location
                 p2.teleport()
                 }
                 **/
                return true;
            }

            return true;
        } catch (Exception e) {
            p.sendMessage(Component.text("알수없는 오류로 인해서 매치 시작에 실패하였습니다, " + e.getLocalizedMessage()).color(NamedTextColor.RED));
        }
        return true;
    }
}
