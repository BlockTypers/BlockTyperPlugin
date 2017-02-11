package com.blocktyper.v1_2_1.plugin;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.v1_2_1.BlockTyperCommand;
import com.blocktyper.v1_2_1.BlockTyperListener;
import com.blocktyper.v1_2_1.IBlockTyperPlugin;
import com.blocktyper.v1_2_1.config.BlockTyperConfig;
import com.blocktyper.v1_2_1.helpers.ClickedBlockHelper;
import com.blocktyper.v1_2_1.helpers.IClickedBlockHelper;
import com.blocktyper.v1_2_1.helpers.IPlayerHelper;
import com.blocktyper.v1_2_1.helpers.IVillagerHelper;
import com.blocktyper.v1_2_1.helpers.InvisibleLoreHelper;
import com.blocktyper.v1_2_1.helpers.PlayerHelper;
import com.blocktyper.v1_2_1.helpers.VillagerHelper;

abstract class BlockTyperPlugin extends JavaPlugin implements IBlockTyperPlugin {

	protected BlockTyperConfig config;

	protected List<String> initMessages = null;

	IPlayerHelper playerHelper;
	
	IVillagerHelper villagerHelper;

	InvisibleLoreHelper invisibleLoreHelper;

	IClickedBlockHelper clickedBlockHelper;

	public BlockTyperPlugin() {
		super();
		this.config = BlockTyperConfig.getConfig(this);
		playerHelper = new PlayerHelper(this);
		villagerHelper = new VillagerHelper(this);
		invisibleLoreHelper = new InvisibleLoreHelper(this);
		clickedBlockHelper = new ClickedBlockHelper(this);
	}

	public BlockTyperConfig config() {
		return config;
	}

	public IPlayerHelper getPlayerHelper() {
		return playerHelper;
	}

	public IVillagerHelper getVillagerHelper() {
		return villagerHelper;
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
	
	public <T extends BlockTyperListener> T registerListener(Class<T> type){
		return BlockTyperListener.getRegisteredInstance(this, type);
	}
	
	public <T extends BlockTyperCommand> T registerCommand(String commandName, Class<T> type){
		return BlockTyperCommand.getRegisteredInstance(commandName, this, type);
	}
	
	
	
	

}