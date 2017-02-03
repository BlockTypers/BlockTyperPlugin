package com.blocktyper.v1_1_8;

import org.bukkit.event.Listener;

public abstract class BlockTyperListener extends BlockTyperUtility implements Listener {
	
	public void register(){
		registerListener(this);
	}
	
	public static <T extends BlockTyperListener> BlockTyperListener getRegisteredInstance(IBlockTyperPlugin plugin, Class<T> type){
		T inst = type.cast(BlockTyperUtility.getInitializedInstance(plugin, type));
		inst.register();
		return inst;
	}
}
