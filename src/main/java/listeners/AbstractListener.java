package listeners;

import org.bukkit.event.Listener;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class AbstractListener implements Listener {
	protected IBlockTyperPlugin plugin;

	public AbstractListener(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
