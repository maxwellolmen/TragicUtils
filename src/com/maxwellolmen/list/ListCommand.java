package com.maxwellolmen.list;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pizzaguy.plugin.Plugin;
import com.pizzaguy.plugin.SettingsManager;

public class ListCommand implements CommandExecutor {
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		
		for (Player p : (ArrayList<Player>) players.clone()) {
			if (SettingsManager.getHides().<ArrayList<String>>get("hides").contains(p.getUniqueId().toString())) {
				players.remove(p);
			}
		}
		
		ArrayList<String> groups = new ArrayList<String>();
		
		for (Player p : players) {
			String[] gs = Plugin.getPerms().getPlayerGroups(p);
			
			for (String g : gs) {
				if (!groups.contains(g)) {
					groups.add(g);
				}
			}
		}
		
		ArrayList<ArrayList<String>> pgroups = new ArrayList<ArrayList<String>>();
		
		for (String group : groups) {
			ArrayList<String> g = new ArrayList<String>();
			
			for (Player p : players) {
				if (arrayContains(Plugin.getPerms().getPlayerGroups(p), group)) {
					g.add(p.getName());
				}
			}
			
			pgroups.add(g);
		}
		
		sender.sendMessage(ChatColor.GOLD + "There are " + ChatColor.RED + players.size() + ChatColor.GOLD + " out of maximum " + ChatColor.RED + Bukkit.getMaxPlayers() + ChatColor.GOLD + " players online.");
		
		for (int i = 0; i < pgroups.size(); i++) {
			ArrayList<String> pgroup = pgroups.get(i);
			
			String ps = pgroup.get(0);
			
			pgroup.remove(ps);
			
			for (String s : pgroup) {
				ps+=", " + s;
			}
			
			sender.sendMessage(ChatColor.GOLD + groups.get(i) + ChatColor.WHITE + ": " + ps);
		}
		
		return false;
	}
	
	public static <T> boolean arrayContains(T[] os, T o) {
		for (T t : os) {
			if (t.equals(o)) {
				return true;
			}
		}
		
		return false;
	}
}
