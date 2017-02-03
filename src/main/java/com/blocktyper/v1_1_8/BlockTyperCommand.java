package com.blocktyper.v1_1_8;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BlockTyperCommand extends BlockTyperUtility implements CommandExecutor {

	public void register(String commandName) {
		plugin.registerCommand(commandName, this);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return false;
	}

	public static <T extends BlockTyperCommand> BlockTyperCommand getRegisteredInstance(String commandName, IBlockTyperPlugin plugin, Class<T> type) {
		T inst = type.cast(BlockTyperUtility.getInitializedInstance(plugin, type));
		inst.register(commandName);
		return inst;
	}
	
	
}
