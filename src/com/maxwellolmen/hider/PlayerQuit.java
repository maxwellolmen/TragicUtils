package com.maxwellolmen.hider;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pizzaguy.plugin.SettingsManager;

public class PlayerQuit implements Listener {
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if (!SettingsManager.getHides().contains("hides")) {
			return;
		}
		
		if (SettingsManager.getHides().<ArrayList<String>>get("hides").contains(p.getUniqueId().toString())) {
			e.setQuitMessage("");
		}
	}
}
