package com.pizzaguy.spectate;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.pizzaguy.plugin.Plugin;


public class SpectateCommand implements CommandExecutor {
    
    private Plugin plugin;
    
    public SpectateCommand(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equals("spectate")) {
                if (player.hasPermission("spectate.allow")) {
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        if (player.hasMetadata("SpectateLite")) {
                            List<MetadataValue> data =  player.getMetadata("SpectateLite");
                            if(GameMode.valueOf(data.get(0).asString()) != GameMode.SPECTATOR){
                                player.setGameMode(GameMode.valueOf(data.get(0).asString()));
                            } else {
                                player.setGameMode(GameMode.SURVIVAL);
                            }
                            player.removeMetadata("SpetctateLite", plugin);
                        } else {
                            player.setMetadata("SpectateLite", new FixedMetadataValue(plugin, player.getGameMode().toString()));
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    } else {
                        player.setMetadata("SpectateLite", new FixedMetadataValue(plugin, player.getGameMode().toString()));
                        player.setGameMode(GameMode.SPECTATOR);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "you dont have permission");
                }
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Player command only.");
            return true;
        }
    }
}
