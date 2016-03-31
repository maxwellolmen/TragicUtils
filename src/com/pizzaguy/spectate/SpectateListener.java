package com.pizzaguy.spectate;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import com.pizzaguy.plugin.Plugin;

public class SpectateListener implements Listener {
    
    private Plugin plugin;
    
    public SpectateListener(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().hasMetadata("SpectateLite")) {
            List<MetadataValue> data = e.getPlayer().getMetadata("SpectateLite");
            if (GameMode.valueOf(data.get(0).asString()) != GameMode.SPECTATOR) {
                e.getPlayer().setGameMode(GameMode.valueOf(data.get(0).asString()));
            } else {
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
            e.getPlayer().removeMetadata("SpectateLite", plugin);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (e.getPlayer().hasMetadata("SpectateLite")) {
            List<MetadataValue> data = e.getPlayer().getMetadata("SpectateLite");
            if (GameMode.valueOf(data.get(0).asString()) != GameMode.SPECTATOR) {
                e.getPlayer().setGameMode(GameMode.valueOf(data.get(0).asString()));
            } else {
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
            e.getPlayer().removeMetadata("SpectateLite", plugin);
        }
    }

}
