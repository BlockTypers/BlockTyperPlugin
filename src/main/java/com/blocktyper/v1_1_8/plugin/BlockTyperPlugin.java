package com.blocktyper.v1_1_8.plugin;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.v1_1_8.BlockTyperCommand;
import com.blocktyper.v1_1_8.BlockTyperListener;
import com.blocktyper.v1_1_8.IBlockTyperPlugin;
import com.blocktyper.v1_1_8.config.BlockTyperConfig;
import com.blocktyper.v1_1_8.helpers.ClickedBlockHelper;
import com.blocktyper.v1_1_8.helpers.IClickedBlockHelper;
import com.blocktyper.v1_1_8.helpers.IPlayerHelper;
import com.blocktyper.v1_1_8.helpers.InvisibleLoreHelper;
import com.blocktyper.v1_1_8.helpers.PlayerHelper;

abstract class BlockTyperPlugin extends JavaPlugin implements IBlockTyperPlugin {

	protected BlockTyperConfig config;

	protected List<String> initMessages = null;

	IPlayerHelper playerHelper;

	InvisibleLoreHelper invisibleLoreHelper;

	IClickedBlockHelper clickedBlockHelper;

	public BlockTyperPlugin() {
		super();
		this.config = BlockTyperConfig.getConfig(this);
		playerHelper = new PlayerHelper(this);
		invisibleLoreHelper = new InvisibleLoreHelper(this);
		clickedBlockHelper = new ClickedBlockHelper(this);
	}

	public BlockTyperConfig config() {
		return config;
	}

	public IPlayerHelper getPlayerHelper() {
		return playerHelper;
	}

	public InvisibleLoreHelper getInvisibleLoreHelper() {
		return invisibleLoreHelper;
	}

	public IClickedBlockHelper getClickedBlockHelper() {
		return clickedBlockHelper;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		if (initMessages != null) {
			for (String msg : initMessages) {
				info(msg);
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@Override
	public void registerCommand(String commandName, CommandExecutor commandExecutor) {
		getCommand(commandName).setExecutor(commandExecutor);
	}

	public class BlockTyperPluginException extends Exception {
		private static final long serialVersionUID = 201610090027L;

		public BlockTyperPluginException(String message) {
			super(message);
		}
	}

	@Override
	public void init(IBlockTyperPlugin plugin) {
		throw new NotImplementedException();
	}
	
	public <T extends BlockTyperListener> BlockTyperListener registerListener(Class<T> type){
		return BlockTyperListener.getRegisteredInstance(this, type);
	}
	
	public <T extends BlockTyperCommand> BlockTyperCommand registerCommand(String commandName, Class<T> type){
		return BlockTyperCommand.getRegisteredInstance(commandName, this, type);
	}

}
