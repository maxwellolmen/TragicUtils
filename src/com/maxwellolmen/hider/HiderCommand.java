package com.maxwellolmen.hider;

import java.util.ArrayList;

import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.pizzaguy.plugin.SettingsManager;

public class HiderCommand implements CommandExecutor {
	private static ArrayList<String> hidden;
	
	public HiderCommand(ArrayList<String> hidden) {
		HiderCommand.hidden = hidden;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "That is a player command.");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (!p.hasPermission(new Permission("hider.hide"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission.");
			return false;
		}
		
		if (args.length >= 1) {
			if (!p.hasPermission(new Permission("hider.hideother"))) {
				p.sendMessage(ChatColor.RED + "You are not allowed to hide other players.");
				return false;
			}
			
			if (Bukkit.getServer().getPlayer(args[0]) == null) {
				p.sendMessage(ChatColor.RED + "That player is not online.");
				return false;
			}
			
			p.sendMessage(ChatColor.AQUA + "Hid player " + args[0] + ".");
			
			p = Bukkit.getServer().getPlayer(args[0]);
		}
		
		if (hidden.contains(p.getUniqueId().toString())) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.showPlayer(p);
				showTab(p);
			}
			
			p.sendMessage(ChatColor.GREEN + "You have been shown.");
			
			hidden.remove(p.getUniqueId().toString());
			
			updateConfig();
			
			return true;
		} else {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.hidePlayer(p);
				hideTab(p);
			}
			
			p.sendMessage(ChatColor.GREEN + "You have been hidden.");
			
			hidden.add(p.getUniqueId().toString());
			
			updateConfig();
			
			return true;
		}
	}
	
	public ArrayList<String> getHides() {
		return hidden;
	}
	
	public static void hideTab(Player p) {
		PacketPlayOutPlayerInfo tablist = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(tablist);
		}
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(tablist);
	}

	public static void showTab(Player p) {
		PacketPlayOutPlayerInfo tablist = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle());
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(tablist);
		}
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(tablist);
	}
	
	public void updateConfig() {
		SettingsManager.getHides().set("hides", hidden);
		SettingsManager.getHides().save();
	}
}
