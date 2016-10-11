package com.blocktyper.plugin;

import org.bukkit.scheduler.BukkitRunnable;

public class DataBackupTask extends BukkitRunnable{
	
	private BlockTyperPlugin plugin;
	
	public DataBackupTask(BlockTyperPlugin plugin){
		this.plugin = plugin;
	}
	public void run() {
		plugin.debugInfo("start DataBackupTask", BlockTyperPlugin.DASHES_TOP);
		

		if (plugin.getAllData() == null || plugin.getAllData().isEmpty()) {
			plugin.debugInfo("No data to write");
			return;
		}

		plugin.debugInfo("Serializing data");

		for (String key : plugin.getAllData().keySet()) {
			plugin.debugInfo("  -" + key);
			plugin.setData(key, plugin.getAllData().get(key), true);
		}
		plugin.debugInfo("Done serializing data");

		plugin.debugInfo("end DataBackupTask", BlockTyperPlugin.DASHES_BOTTOM);
	}
}
