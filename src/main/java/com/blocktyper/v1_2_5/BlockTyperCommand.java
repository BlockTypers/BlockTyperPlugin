package com.blocktyper.v1_2_5;

import org.bukkit.command.CommandExecutor;

public abstract class BlockTyperCommand extends BlockTyperUtility implements CommandExecutor {

	public void register(String commandName) {
		plugin.registerCommand(commandName, this);
	}

	public static <T extends BlockTyperCommand> T getRegisteredInstance(String commandName, IBlockTyperPlugin plugin,
			Class<T> type) {
		T inst = type.cast(BlockTyperUtility.getInitializedInstance(plugin, type));
		inst.register(commandName);
		return inst;
	}

}
