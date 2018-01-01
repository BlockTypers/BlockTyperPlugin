package com.blocktyper.v1_2_4;

import org.bukkit.event.Listener;

public abstract class BlockTyperListener extends BlockTyperUtility implements Listener {

	public void register() {
		plugin.registerListener(this);
	}

	public static <T extends BlockTyperListener> T getRegisteredInstance(IBlockTyperPlugin plugin, Class<T> type) {
		T inst = type.cast(BlockTyperUtility.getInitializedInstance(plugin, type));
		inst.register();
		return inst;
	}

}
