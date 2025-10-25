package com.songro.ouw.command;

import com.songro.ouw.Ouw;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SetupDuel implements CommandExecutor {
    Ouw plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        plugin = Ouw.plugin;
        FileConfiguration config = Ouw.plugin.getConfigData();
        Player p = (Player) commandSender;
        if(args.length != 9) {
            p.sendMessage(Component.text("Invalid command usage").color(NamedTextColor.YELLOW));
            return false;
        }

        boolean ignoreSpawnWarning = Ouw.plugin.getConfigData().getBoolean("ignore_spawn_warn");
        boolean keepWorldBorder = Boolean.parseBoolean(args[8]);
        boolean copyDefaultSpawn = Boolean.parseBoolean(args[7]);
        boolean copyWorldGameRule = Boolean.parseBoolean(args[6]);
        Location player_1_spawn = new Location(p.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Location player_2_spawn = new Location(p.getWorld(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        World world = p.getWorld();
        if(!ignoreSpawnWarning && Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[4]) < 0) {
            p.sendMessage(Component.text("스폰 장소가 안전하지 않다고 판단하였습니다, 정말로 알고 진행하시는건가요? (스폰장소가 0보다 작습니다!)").color(NamedTextColor.YELLOW));
            if (!ignoreSpawnWarning && world.getBlockAt(player_1_spawn).getType().isAir() || world.getBlockAt(player_2_spawn).getType().isAir()) {
                p.sendMessage(Component.text("스폰 장소가 안전하지 않다고 판단하였습니다, 정말로 알고 진행하시는건가요? (스폰블럭이 공기블럭)").color(NamedTextColor.YELLOW));
            }
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(copyDefaultSpawn) config.set("worlds." + world.getName() + ".copyDefaultSpawn", copyDefaultSpawn);
            if(copyWorldGameRule) config.set("worlds." + world.getName() + ".copyWorldGameRule", copyWorldGameRule);
            if(keepWorldBorder) config.set("worlds." + world.getName() + ".keepWorldBorder", keepWorldBorder);
            config.set("worlds." + world.getName() + ".player1_spawn", player_1_spawn);
            config.set("worlds." + world.getName() + ".player2_spawn", player_2_spawn);

            try {
                config.save(Ouw.plugin.config_file);
                p.sendMessage(Component.text("월드 설정파일이 저장되었습니다.").color(NamedTextColor.GREEN));
            } catch (IOException e) {
                p.sendMessage(Component.text("월드 저장에 실패하였습니다, 설정 파일이 다른 프로그램에 의해 열려져있나요?").color(NamedTextColor.RED));
            }
        });

        return true;
    }
}
