package com.maxwellolmen.keepitems;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		if (p.hasPermission("keep-items.keep")) {
			e.setKeepInventory(true);
		}
		
		if (p.hasPermission("keep-items.keepxp")) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}
}
