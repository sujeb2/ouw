package com.songro.ouw.command;

import com.songro.ouw.Ouw;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.external.jakarta.inject.Named;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SetupDuelKit implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player p = (Player) commandSender;
        Logger log = Ouw.log;

        if(args.length < 1) {
            p.sendMessage(Component.text("Invalid command usage").color(NamedTextColor.RED));
            return false;
        }

        boolean copyArmor = Boolean.parseBoolean(args[0]);
        String worldName = p.getWorld().getName();
        Inventory inv = p.getInventory();
        ItemStack[] content = inv.getContents();
        List<ItemStack> armorList = new ArrayList<>();
        List<ItemStack> mainItemList = new ArrayList<>();

        for(int i=0; i<content.length; i++) {
            try {
                if (copyArmor && content[i].getType().isItem() && i >= 9 && i <= 12) {
                    armorList.set(i, content[i]);
                    log.info("Armor added to list, returning information: " +
                            "\nItemType: " + content[i].getType().name() +
                            "\nItemName (or display name): " + (content[i].getItemMeta().hasItemName() ? content[i].getItemMeta().itemName() : content[i].getItemMeta().displayName()));
                } else {
                    mainItemList.set(i, content[i]);
                    log.info("Item added to list, returning information: " +
                            "\nItemType: " + content[i].getType().name() +
                            "\nItemName (or display name): " + (content[i].getItemMeta().hasItemName() ? content[i].getItemMeta().itemName() : content[i].getItemMeta().displayName()));
                }
            } catch (Exception e) {
                log.severe("Failed to gather item data!! Is item valid to save?");
                e.printStackTrace();
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(Ouw.plugin, () -> {
            try {
                Ouw.plugin.getKitData().set(worldName+".armorList", armorList);
                Ouw.plugin.getKitData().set(worldName+".itemList", mainItemList);

                Ouw.plugin.getConfigData().save(Ouw.plugin.kit_file);
                p.sendMessage(Component.text("Successfully saved kit data.").color(NamedTextColor.GREEN));
            } catch (IOException e) {
                p.sendMessage(Component.text("Failed to save kit data to config!!\n" + e.getLocalizedMessage()).color(NamedTextColor.RED));
            }
        });

        return true;
    }
}
