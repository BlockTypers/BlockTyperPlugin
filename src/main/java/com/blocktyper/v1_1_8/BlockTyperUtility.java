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

public abstract class BlockTyperUtility implements IBlockTyperUtility {
	protected IBlockTyperPlugin plugin;

	@Override
	public void init(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	public static <T extends IBlockTyperUtility> IBlockTyperUtility getInitializedInstance(IBlockTyperPlugin plugin,
			Class<T> type) {
		try {
			T inst = type.newInstance();
			inst.init(plugin);
			return inst;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void registerCommand(String commandName, CommandExecutor commandExecutor) {
		plugin.registerCommand(commandName, commandExecutor);
	}

	protected void registerListener(Listener listener) {
		plugin.registerListener(listener);
	}

	protected String getRecipesNbtKey() {
		return plugin.getRecipesNbtKey();
	}

	protected String getLocalizedMessage(String key) {
		return plugin.getLocalizedMessage(key);
	}

	protected String getLocalizedMessage(String key, HumanEntity player) {
		return plugin.getLocalizedMessage(key, player);
	}

	protected String getLocalizedMessage(String key, String localeCode) {
		return plugin.getLocalizedMessage(key, localeCode);
	}

	protected ResourceBundle getBundle(Locale locale) {
		return plugin.getBundle(locale);
	}

	protected BlockTyperConfig config() {
		return plugin.config();
	}

	protected IBlockTyperRecipeRegistrar recipeRegistrar() {
		return plugin.recipeRegistrar();
	}

	protected <T> T getObject(ItemStack item, String key, Class<T> type) {
		return plugin.getObject(item, key, type);
	}

	protected IPlayerHelper getPlayerHelper() {
		return plugin.getPlayerHelper();
	}

	protected IClickedBlockHelper getClickedBlockHelper() {
		return plugin.getClickedBlockHelper();
	}

	protected void info(String info) {
		plugin.info(info);
	}

	protected void info(String warning, Integer mode) {
		plugin.info(warning, mode);
	}

	protected void info(String warning, Integer mode, Integer stackTraceCount) {
		plugin.info(warning, mode, stackTraceCount);
	}

	protected void warning(String warning) {
		plugin.warning(warning);
	}

	protected void warning(String warning, Integer mode) {
		plugin.warning(warning, mode);
	}

	protected void warning(String warning, Integer mode, Integer stackTraceCount) {
		plugin.warning(warning, mode, stackTraceCount);
	}

	protected void debugInfo(String info) {
		plugin.debugInfo(info);
	}

	protected void debugInfo(String warning, Integer mode) {
		plugin.debugInfo(warning, mode);
	}

	protected void debugInfo(String warning, Integer mode, Integer stackTraceCount) {
		plugin.debugInfo(warning, mode, stackTraceCount);
	}

	protected void debugWarning(String warning) {
		plugin.debugWarning(warning);
	}

	protected void debugWarning(String warning, Integer mode) {
		plugin.debugWarning(warning, mode);
	}

	protected void debugWarning(String warning, Integer mode, Integer stackTraceCount) {
		plugin.debugWarning(warning, mode, stackTraceCount);
	}

	protected void section(boolean isWarning) {
		plugin.section(isWarning);
	}

	protected void section(boolean isWarning, String line) {
		plugin.section(isWarning, line);
	}

	protected boolean setData(String key, Object value, boolean flush) {
		return plugin.setData(key, value, flush);
	}

	protected boolean setData(String key, Object value) {
		return plugin.setData(key, value);
	}

	protected Map<String, Object> getAllData() {
		return plugin.getAllData();
	}

	protected <T> T getTypeData(String key, Class<T> type) {
		return plugin.getTypeData(key, type);
	}

	protected <T> T deserializeJsonSafe(String json, Class<T> type) {
		return plugin.deserializeJsonSafe(json, type);
	}

	protected InvisibleLoreHelper getInvisibleLoreHelper() {
		return plugin.getInvisibleLoreHelper();
	}
}
