package com.maxwellolmen.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinTab implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		TabAPI.labelName(e.getPlayer());
	}
}
