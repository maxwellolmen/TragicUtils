package com.pizzaguy.plugin;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.maxwellolmen.hider.HiderCommand;
import com.maxwellolmen.hider.PlayerJoinHider;
import com.maxwellolmen.hider.PlayerQuit;
import com.maxwellolmen.keepitems.PlayerDeath;
import com.maxwellolmen.list.ListCommand;
import com.maxwellolmen.tablist.PlayerChangedWorld;
import com.maxwellolmen.tablist.PlayerJoinTab;
import com.pizzaguy.frameprotect.FrameProtectListener;
import com.pizzaguy.frameprotect.FrameProtectSql;
import com.pizzaguy.itemmail.ItemMailCommand;
import com.pizzaguy.itemmail.ItemMailSql;
import com.pizzaguy.itemmail.ItemMailUI;
import com.pizzaguy.itemmail.ItemMailUIListener;
import com.pizzaguy.itemmail.Transaction;
import com.pizzaguy.lore.LoreCommand;
import com.pizzaguy.lore.NameCommand;
import com.pizzaguy.spectate.SpectateCommand;
import com.pizzaguy.spectate.SpectateListener;
import com.pizzaguy.ticket.TicketCommand;
import com.pizzaguy.ticket.TicketListener;
import com.pizzaguy.ticket.TicketSql;

public class Plugin extends JavaPlugin {
	private static HiderCommand hc;
	private static Permission permission;

    @Override
    public void onEnable() {
    	setupPermissions();
    	
        // lore
        getCommand("lore").setExecutor(new LoreCommand());
        // name
        getCommand("Name").setExecutor(new NameCommand());
        // spectate
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        Bukkit.getPluginManager().registerEvents(new SpectateListener(this), this);
        // tickets
        TicketSql tiSql = new TicketSql(this);
        getCommand("ticket").setExecutor(new TicketCommand(this, tiSql));
        Bukkit.getPluginManager().registerEvents(new TicketListener(this, tiSql), this);
        // itemmail
        ItemMailSql imSql = new ItemMailSql(this);
        getCommand("itemmail").setExecutor(new ItemMailCommand(this, imSql));
        // hider
        Plugin.hc = new HiderCommand(SettingsManager.getHides().contains("hides") ? SettingsManager.getHides().<ArrayList<String>>get("hides") : new ArrayList<String>());
        getCommand("hide").setExecutor(hc);
        // list
        getCommand("list").setExecutor(new ListCommand());
        
        Bukkit.getPluginManager().registerEvents(new ItemMailUIListener(imSql), this);
        // frame protect
        FrameProtectSql fpSql = new FrameProtectSql(this);
        Bukkit.getPluginManager().registerEvents(new FrameProtectListener(fpSql), this);
        // keep items
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        // hider
        Bukkit.getPluginManager().registerEvents(new PlayerJoinHider(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        // tablist
        if (!SettingsManager.getWorlds().contains("replacements")) {
        	SettingsManager.getWorlds().set("replacements", new ArrayList<String>());
        }
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorld(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinTab(), this);
        getCommand("tureload").setExecutor(new ReloadCommand());
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory() != null)
                if (player.getOpenInventory().getTopInventory().getSize() == ItemMailUI.SIZE)
                    if (player.getOpenInventory().getTopInventory().getTitle().equals(ItemMailUI.CONCEPT))
                        Transaction.giveItems(player, ItemMailUI.getConceptItems(player.getOpenInventory().getTopInventory()));
        }

        SettingsManager.getHides().set("hides", hc.getHides());
        SettingsManager.getHides().save();
    }
    
    public static org.bukkit.plugin.Plugin getPlugin() {
    	return Bukkit.getPluginManager().getPlugin("TragicUtilities");
    }
    
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    
    public static Permission getPerms() {
    	return permission;
    }
}
