package com.pizzaguy.ticket;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.pizzaguy.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class TicketListener implements Listener {

    @SuppressWarnings("unused")
	private Plugin plugin;
    private TicketSql sql;

    public TicketListener(Plugin plugin, TicketSql sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("ticket.select")) {
            int i = sql.getTicketCount();
            e.getPlayer().sendMessage(ChatColor.GOLD + "There are " + ChatColor.RED + i + " tickets");
        }
    }

}
