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

public interface IBlockTyperUtility {

	void init(IBlockTyperPlugin plugin);

	void registerListener(Listener listener);

	public void registerCommand(String commandName, CommandExecutor commandExecutor);

	String getRecipesNbtKey();

	String getLocalizedMessage(String key);

	String getLocalizedMessage(String key, HumanEntity player);

	String getLocalizedMessage(String key, String localeCode);

	ResourceBundle getBundle(Locale locale);

	BlockTyperConfig config();

	IBlockTyperRecipeRegistrar recipeRegistrar();

	<T> T getObject(ItemStack item, String key, Class<T> type);

	IPlayerHelper getPlayerHelper();

	IClickedBlockHelper getClickedBlockHelper();

	void info(String info);

	void info(String warning, Integer mode);

	void info(String warning, Integer mode, Integer stackTraceCount);

	void warning(String warning);

	void warning(String warning, Integer mode);

	void warning(String warning, Integer mode, Integer stackTraceCount);

	void debugInfo(String info);

	void debugInfo(String warning, Integer mode);

	void debugInfo(String warning, Integer mode, Integer stackTraceCount);

	void debugWarning(String warning);

	void debugWarning(String warning, Integer mode);

	void debugWarning(String warning, Integer mode, Integer stackTraceCount);

	void section(boolean isWarning);

	void section(boolean isWarning, String line);

	boolean setData(String key, Object value, boolean flush);

	boolean setData(String key, Object value);

	Map<String, Object> getAllData();

	<T> T getTypeData(String key, Class<T> type);

	<T> T deserializeJsonSafe(String json, Class<T> type);

	InvisibleLoreHelper getInvisibleLoreHelper();

	// static constants
	public static final String EMPTY = "";
	public static final String DASHES = "-----------------------------------";
	public static final String HASHES = "###################################";
	public static final int DASHES_TOP = 1;
	public static final int DASHES_BOTTOM = 2;
	public static final int DASHES_TOP_AND_BOTTOM = 3;
	public static final int METHOD_NAME = 4;
	public static final int DEFAULT_WARNING_STACK_TRACE_COUNT = -1;

}
