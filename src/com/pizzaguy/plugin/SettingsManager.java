package com.pizzaguy.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
	
	private SettingsManager() { }
   
	private static SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	private static SettingsManager hides = new SettingsManager("hides"), worlds = new SettingsManager("worlds");
	
	public static SettingsManager getHides() {
		return hides;
	}
	
	public static SettingsManager getWorlds() {
		return worlds;
	}
	
	private File file;
	private FileConfiguration config;
	
	private SettingsManager(String fileName) {
		if (!Plugin.getPlugin().getDataFolder().exists()) {
			Plugin.getPlugin().getDataFolder().mkdir();
		}
		
		file = new File(Plugin.getPlugin().getDataFolder(), fileName + ".yml");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String path) {
		return (T) config.get(path);
	}
	
	public Set<String> getKeys() {
		return config.getKeys(false);
	}
	
	public void set(String path, Object value) {
		config.set(path, value);
		save();
	}
	
	public boolean contains(String path){
		return config.contains(path);
	}
	
	public ConfigurationSection createSection(String path) {
		ConfigurationSection section = config.createSection(path);
		save();
		return section;
	}
	
	public void save() {
		try {
			config.save(file);
		} catch (Exception e){
			Plugin.getPlugin().getLogger().warning("Error while saving yml file.");
		}
	}
	
	public void reload() {
	    config = YamlConfiguration.loadConfiguration(file);
	    
	    try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
