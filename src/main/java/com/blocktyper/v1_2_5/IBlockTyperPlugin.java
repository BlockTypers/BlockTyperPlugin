package com.blocktyper.v1_2_5;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.blocktyper.v1_2_5.config.BlockTyperConfig;
import com.blocktyper.v1_2_5.helpers.IClickedBlockHelper;
import com.blocktyper.v1_2_5.helpers.IPlayerHelper;
import com.blocktyper.v1_2_5.helpers.IVillagerHelper;
import com.blocktyper.v1_2_5.helpers.InvisHelper;
import com.blocktyper.v1_2_5.recipes.IBlockTyperRecipeRegistrar;
import com.blocktyper.v1_2_5.recipes.IRecipe;

public interface IBlockTyperPlugin extends Plugin, IBlockTyperUtility {

	void onPrepareItemCraft(PrepareItemCraftEvent event);

	void onCraftItem(CraftItemEvent event);
	
	IRecipe bootstrapRecipe(IRecipe recipe);

	String getRecipeKey(ItemStack item);
	
	<T extends BlockTyperListener> BlockTyperListener registerListener(Class<T> type);
	
	<T extends BlockTyperCommand> BlockTyperCommand registerCommand(String commandName, Class<T> type);

	void registerListener(Listener listener);

	public void registerCommand(String commandName, CommandExecutor commandExecutor);

	String getRecipesNbtKey();
	
	public boolean itemHasExpectedNbtKey(ItemStack item, String recipeKey);

	String getLocalizedMessage(String key);

	String getLocalizedMessage(String key, HumanEntity player);

	String getLocalizedMessage(String key, String localeCode);

	ResourceBundle getBundle(Locale locale);

	BlockTyperConfig config();

	IBlockTyperRecipeRegistrar recipeRegistrar();

	<T> T getObject(ItemStack item, String key, Class<T> type);

	IPlayerHelper getPlayerHelper();
	
	IVillagerHelper getVillagerHelper();

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

	InvisHelper getInvisHelper();

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
