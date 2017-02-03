package com.blocktyper.v1_1_8;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_1_8.config.BlockTyperConfig;
import com.blocktyper.v1_1_8.helpers.IClickedBlockHelper;
import com.blocktyper.v1_1_8.helpers.IPlayerHelper;
import com.blocktyper.v1_1_8.helpers.InvisibleLoreHelper;
import com.blocktyper.v1_1_8.recipes.IBlockTyperRecipeRegistrar;

public class BlockTyperUtility implements IBlockTyperUtility {
	protected IBlockTyperPlugin plugin;

	@Override
	public void init(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void registerCommand(String commandName, CommandExecutor commandExecutor) {
		plugin.registerCommand(commandName, commandExecutor);
	}
	
	@Override
	public void registerListener(Listener listener) {
		plugin.registerListener(listener);
	}

	@Override
	public String getRecipesNbtKey() {
		return plugin.getRecipesNbtKey();
	}

	@Override
	public String getLocalizedMessage(String key) {
		return plugin.getLocalizedMessage(key);
	}

	@Override
	public String getLocalizedMessage(String key, HumanEntity player) {
		return plugin.getLocalizedMessage(key, player);
	}

	@Override
	public String getLocalizedMessage(String key, String localeCode) {
		return plugin.getLocalizedMessage(key, localeCode);
	}

	@Override
	public ResourceBundle getBundle(Locale locale) {
		return plugin.getBundle(locale);
	}

	@Override
	public BlockTyperConfig config() {
		return plugin.config();
	}

	@Override
	public IBlockTyperRecipeRegistrar recipeRegistrar() {
		return plugin.recipeRegistrar();
	}

	@Override
	public <T> T getObject(ItemStack item, String key, Class<T> type) {
		return plugin.getObject(item, key, type);
	}

	@Override
	public IPlayerHelper getPlayerHelper() {
		return plugin.getPlayerHelper();
	}

	@Override
	public IClickedBlockHelper getClickedBlockHelper() {
		return plugin.getClickedBlockHelper();
	}

	@Override
	public void info(String info) {
		plugin.info(info);
	}

	@Override
	public void info(String warning, Integer mode) {
		plugin.info(warning, mode);
	}

	@Override
	public void info(String warning, Integer mode, Integer stackTraceCount) {
		plugin.info(warning, mode, stackTraceCount);
	}

	@Override
	public void warning(String warning) {
		plugin.warning(warning);
	}

	@Override
	public void warning(String warning, Integer mode) {
		plugin.warning(warning, mode);
	}

	@Override
	public void warning(String warning, Integer mode, Integer stackTraceCount) {
		plugin.warning(warning, mode, stackTraceCount);
	}

	@Override
	public void debugInfo(String info) {
		plugin.debugInfo(info);
	}

	@Override
	public void debugInfo(String warning, Integer mode) {
		plugin.debugInfo(warning, mode);
	}

	@Override
	public void debugInfo(String warning, Integer mode, Integer stackTraceCount) {
		plugin.debugInfo(warning, mode, stackTraceCount);
	}

	@Override
	public void debugWarning(String warning) {
		plugin.debugWarning(warning);
	}

	@Override
	public void debugWarning(String warning, Integer mode) {
		plugin.debugWarning(warning, mode);
	}

	@Override
	public void debugWarning(String warning, Integer mode, Integer stackTraceCount) {
		plugin.debugWarning(warning, mode, stackTraceCount);
	}

	@Override
	public void section(boolean isWarning) {
		plugin.section(isWarning);
	}

	@Override
	public void section(boolean isWarning, String line) {
		plugin.section(isWarning, line);
	}

	@Override
	public boolean setData(String key, Object value, boolean flush) {
		return plugin.setData(key, value, flush);
	}

	@Override
	public boolean setData(String key, Object value) {
		return plugin.setData(key, value);
	}

	@Override
	public Map<String, Object> getAllData() {
		return plugin.getAllData();
	}

	@Override
	public <T> T getTypeData(String key, Class<T> type) {
		return plugin.getTypeData(key, type);
	}

	@Override
	public <T> T deserializeJsonSafe(String json, Class<T> type) {
		return plugin.deserializeJsonSafe(json, type);
	}

	@Override
	public InvisibleLoreHelper getInvisibleLoreHelper() {
		return plugin.getInvisibleLoreHelper();
	}
	
	
	public static <T extends IBlockTyperUtility> IBlockTyperUtility getInitializedInstance(IBlockTyperPlugin plugin, Class<T> type) {
		Object object = null;
		try {
			object = type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		T inst = type.cast(object);
		inst.init(plugin);
		return inst;
	}
}
