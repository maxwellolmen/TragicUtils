package com.pizzaguy.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SettingsManager.getWorlds().reload();
        sender.sendMessage(ChatColor.AQUA + "Configurations reloaded.");
        return true;
    }
}
