package com.pizzaguy.lore;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command arg, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("lore.add")) {
                if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    System.out.println(p.getInventory().getItemInMainHand());
                    ItemMeta target = p.getInventory().getItemInMainHand().getItemMeta();
                    if (args.length > 0) {
                        List<String> text = new ArrayList<String>();
                        String s = StringUtils.join(args, " ");
                        s = ChatColor.translateAlternateColorCodes('&', s);
                        String[] list = s.split(",");
                        for(int i =0; i <list.length;i++)
                            text.add(list[i].trim());
                        target.setLore(text);
                        p.getInventory().getItemInMainHand().setItemMeta(target);
                        p.sendMessage(ChatColor.GOLD + "You added lore to your item");
                        return true;
                    } else {
                        p.sendMessage(ChatColor.RED + "You need to add text to your lore");
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
