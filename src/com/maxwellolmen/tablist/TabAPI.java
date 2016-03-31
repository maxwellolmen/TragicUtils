package com.maxwellolmen.tablist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.pizzaguy.plugin.SettingsManager;

import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_9_R1.WorldSettings.EnumGamemode;

public class TabAPI {
	public static void updateTab() {
		ArrayList<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		
		World w1 = Bukkit.getWorld(SettingsManager.getWorlds().<String>get("lobby"));
		World w2 = Bukkit.getWorld(SettingsManager.getWorlds().<String>get("survival"));
		World w3 = Bukkit.getWorld(SettingsManager.getWorlds().<String>get("creative"));
		World w4 = Bukkit.getWorld(SettingsManager.getWorlds().<String>get("resource"));
		
		ArrayList<Player> lp = new ArrayList<Player>();
		ArrayList<Player> sp = new ArrayList<Player>();
		ArrayList<Player> cp = new ArrayList<Player>();
		ArrayList<Player> rp = new ArrayList<Player>();
		ArrayList<Player> op = new ArrayList<Player>();
		
		for (Player p : players) {
			if (w1.getPlayers().contains(p)) {
				lp.add(p);
			} else if (w2.getPlayers().contains(p)) {
				sp.add(p);
			} else if (w3.getPlayers().contains(p)) {
				cp.add(p);
			} else if (w4.getPlayers().contains(p)) {
				rp.add(p);
			} else {
				op.add(p);
			}
		}
		
		addPlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Lobby:");
		
		addCatchLP(3, lp);
		
		addCatchLP(7, lp);
		
		addCatchLP(11, lp);
		
		addPlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Survival:");
		
		addCatchSP(3, sp);
		
		addCatchSP(7, sp);
		
		addCatchSP(11, sp);
		
		addPlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Creative:");
		
		addCatchCP(3, cp);
		
		addPlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Resource:");
		
		addCatchRP(3, rp);
		
		addCatchRP(7, rp);
		
		addPlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Other:");
		
		addCatchOP(3, op);
		
		addCatchLP(0, lp);
		
		addCatchLP(4, lp);
		
		addCatchLP(8, lp);
		
		addCatchLP(12, lp);
		
		addCatchSP(0, sp);
		
		addCatchSP(4, sp);
		
		addCatchSP(8, sp);
		
		addCatchSP(12, sp);
		
		addCatchCP(0, cp);
		
		addCatchCP(4, cp);
		
		addCatchRP(0, rp);
		
		addCatchRP(4, rp);
		
		addCatchRP(8, rp);
		
		addCatchOP(0, rp);
		
		addCatchOP(4, op);
		
		addCatchLP(1, lp);
		
		addCatchLP(5, lp);
		
		addCatchLP(9, lp);
		
		addCatchLP(13, lp);
		
		addCatchSP(1, sp);
		
		addCatchSP(5, sp);
		
		addCatchSP(9, sp);
		
		addCatchSP(13, sp);
		
		addCatchCP(1, cp);
		
		addCatchCP(5, cp);
		
		addCatchRP(1, rp);
		
		addCatchRP(5, rp);
		
		addCatchRP(9, rp);
		
		addCatchOP(1, op);
		
		addCatchOP(5, op);
		
		addCatchLP(2, lp);
		
		addCatchLP(6, lp);
		
		addCatchLP(10, lp);
		
		addCatchLP(14, lp);
		
		addCatchSP(2, sp);
		
		addCatchSP(6, sp);
		
		addCatchSP(10, sp);
		
		addCatchSP(14, sp);
		
		addCatchCP(2, cp);
		
		addCatchCP(6, cp);
		
		addCatchRP(2, rp);
		
		addCatchRP(6, rp);
		
		addCatchRP(10, rp);
		
		addCatchOP(2, op);
		
		addCatchOP(6, op);
	}
	
	public static void addCatchLP(int i, ArrayList<Player> lp) {
		try {
			addPlayer(lp.get(i));
		} catch (Exception e) {
			addPlayer("");
		}
	}
	
	public static void addCatchSP(int i, ArrayList<Player> sp) {
		try {
			addPlayer(sp.get(i));
		} catch (Exception e) {
			addPlayer("");
		}
	}
	
	public static void addCatchCP(int i, ArrayList<Player> cp) {
		try {
			addPlayer(cp.get(i));
		} catch (Exception e) {
			addPlayer("");
		}
	}
	
	public static void addCatchRP(int i, ArrayList<Player> rp) {
		try {
			addPlayer(rp.get(i));
		} catch (Exception e) {
			addPlayer("");
		}
	}
	
	public static void addCatchOP(int i, ArrayList<Player> op) {
		try {
			addPlayer(op.get(i));
		} catch (Exception e) {
			addPlayer("");
		}
	}
	
	private static void addPlayer(Player p) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle());
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	private static void addPlayer(String p) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		
		try {
			Field a = packet.getClass().getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, EnumPlayerInfoAction.ADD_PLAYER);
			Field b = packet.getClass().getDeclaredField("b");
			b.setAccessible(true);
			List<PlayerInfoData> dataList = Lists.newArrayList();
			dataList.add(packet.new PlayerInfoData(new GameProfile(UUID.randomUUID(), p), 0, EnumGamemode.SURVIVAL, ChatSerializer.a("{\"text\":\"" + p + "\"}")));
			b.set(packet, dataList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	public static void labelName(Player p) {
		p.setPlayerListName(ChatColor.GRAY + "[" + ChatColor.GREEN + WordUtils.capitalizeFully(replace(p.getWorld().getName())) + ChatColor.GRAY + "]" + ChatColor.WHITE + " " + p.getName());
	}
	
	public static String replace(String s) {
	    ArrayList<String> rs = SettingsManager.getWorlds().<ArrayList<String>>get("replacements");
	    
	    String st = "" + s;
	    
	    for (String r : rs) {
	        if (st.contains(r.split(":")[0])) {
	            st = st.replace(r.split(":")[0], r.split(":")[1]);
	        }
	    }
	    
	    return st;
	}
}
