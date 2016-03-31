package com.pizzaguy.itemmail;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pizzaguy.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class ItemMailCommand implements CommandExecutor {

    @SuppressWarnings("unused")
	private Plugin plugin;
    private ItemMailSql sql;

    public ItemMailCommand(Plugin plugin, ItemMailSql sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is for players only.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Use /itemmail help or /itemmail ? for commands");
            return true;
        }
        if (args[0].equals("send") || args[0].equals("s")) {
            if (!player.hasPermission("itemmail.send")) {
                player.sendMessage(ChatColor.RED + "You dont have permission to do this");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /itemmail send <name>");
                return true;
            }
            OfflinePlayer[] players = Bukkit.getOfflinePlayers().clone();
            int x = -1;
            for (int i = 0; i < players.length; i++)
                if (players[i].getName().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                    x = i;
                    break;
                }
            if (x == -1) {
                player.sendMessage(ChatColor.RED + "Please use a player that has played on the server.");
                return true;
            }
            if(players[x].getUniqueId().equals(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "You cannot send an ItemMail to yourself.");
                return true;
            }
            player.openInventory(ItemMailUI.getConcept(players[x]));
            return true;
        }
        if (args[0].equals("read") || args[0].equals("r")) {
            if (!player.hasPermission("itemmail.read")) {
                player.sendMessage(ChatColor.RED + "You dont have permission to do this");
                return true;
            }
            player.openInventory(ItemMailUI.getInbox(sql.getTransactions(player.getUniqueId())));
            return true;
        }
        if (args[0].equals("help") || args[0].equals("?")) {
            player.sendMessage(ChatColor.GOLD + "---" + ChatColor.WHITE + "ItemMail help" + ChatColor.GOLD + "---\n" + ChatColor.WHITE + "/itemmail send <player>" + ChatColor.GOLD + " to send itemmail\n" + ChatColor.WHITE + "/itemmail read" + ChatColor.GOLD + " to read itemmail\n" + ChatColor.WHITE + "/itemmail help" + ChatColor.GOLD + " to see this\n");

        }
        return false;
    }

}
