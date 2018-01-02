package com.blocktyper.v1_2_6.plugin;

import org.bukkit.scheduler.BukkitRunnable;

public class DataBackupTask extends BukkitRunnable {

	private BlockTyperPlugin plugin;

	public DataBackupTask(BlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	public void run() {
		if (plugin.getAllData() == null || plugin.getAllData().isEmpty()) {
			return;
		}

		for (String key : plugin.getAllData().keySet()) {
			plugin.debugInfo("  -" + key);
			plugin.setData(key, plugin.getAllData().get(key), true);
		}
	}
}
