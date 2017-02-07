package com.blocktyper.v1_1_9.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockTyperConfig {
	protected JavaPlugin plugin;
	protected static Map<String, BlockTyperConfig> config;

	protected BlockTyperConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}

	public static BlockTyperConfig getConfig(JavaPlugin plugin) {
		if (config == null) {
			config = new HashMap<String, BlockTyperConfig>();
		}
		BlockTyperConfig blockTyperConfig = new BlockTyperConfig(plugin);
		config.put(plugin.getName(), blockTyperConfig);

		return blockTyperConfig;
	}

	public void reloadConfig() {
		plugin.reloadConfig();
		config = null;
	}

	///////////////////////////
	// SPECIFIC FIELD HELPERS///
	///////////////////////////
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public String dataFolderName() {
		return plugin.getConfig().getString("data.folder.name", "data");
	}

	public int dataBackupFrequencySec() {
		return plugin.getConfig().getInt("data.backup.frequency.sec", -1);
	}

	public String getLocale() {
		return hasLocaleSetting() && plugin.getConfig().getString("locale") != null
				? plugin.getConfig().getString("locale") : "en";
	}

	public boolean debugEnabled() {
		return plugin.getConfig().getBoolean("debug", false);
	}

	public boolean recipesDisabled() {
		return plugin.getConfig().getBoolean("recipes.disabled", false);
	}

	public boolean logRecipes() {
		return plugin.getConfig().getBoolean("log.recipes.enable", true);
	}

	public boolean isMetricsEnabled() {
		return plugin.getConfig().getBoolean("Metrics", true);
	}

	public boolean hasLocaleSetting() {
		return plugin.getConfig().contains("locale", false);
	}

	public boolean hasDefaultSetting(String key) {
		return plugin.getConfig().getDefaults() != null && plugin.getConfig().getDefaults().contains(key, false);
	}

	public boolean hasSetting(String key) {
		return plugin.getConfig().contains(key, false);
	}
}
