package com.pizzaguy.lore;

import org.bukkit.command.CommandExecutor;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class NameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command arg, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("name.add")) {
                if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    ItemMeta target = p.getInventory().getItemInMainHand().getItemMeta();
                    if (args.length > 0) {
                        String s = StringUtils.join(args, " ");
                        s = ChatColor.translateAlternateColorCodes('&', s);
                        target.setDisplayName(s);
                        p.getInventory().getItemInMainHand().setItemMeta(target);
                        p.sendMessage(ChatColor.GOLD + "You change your item's name.");
                        return true;
                    } else {
                        p.sendMessage(ChatColor.RED + "You need to add text to change your items name..");
                        return true;
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "You dont have permission.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command is player only.");
            return true;
        }
        return false;
    }
}
