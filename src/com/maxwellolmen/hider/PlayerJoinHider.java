package com.maxwellolmen.hider;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.pizzaguy.plugin.SettingsManager;

public class PlayerJoinHider implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if (!SettingsManager.getHides().contains("hides")) {
			return;
		}
		
		if (SettingsManager.getHides().<ArrayList<String>>get("hides").contains(p.getUniqueId().toString())) {
			e.setJoinMessage("");
			
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.hidePlayer(p);
				HiderCommand.hideTab(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin2(PlayerJoinEvent e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!SettingsManager.getHides().contains("hides")) {
				return;
			}
			
			if (SettingsManager.getHides().<ArrayList<String>>get("hides").contains(p.getUniqueId().toString())) {
				e.getPlayer().hidePlayer(p);
				HiderCommand.hideTab(p);
			}
		}
	}
}
