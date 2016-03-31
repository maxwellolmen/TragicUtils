package com.maxwellolmen.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorld implements Listener {
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		TabAPI.labelName(e.getPlayer());
	}
}
