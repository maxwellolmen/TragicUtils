package com.maxwellolmen.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitTab implements Listener {
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		TabAPI.updateTab();
	}
}
