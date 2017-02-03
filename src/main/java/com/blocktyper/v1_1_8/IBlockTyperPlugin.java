package com.blocktyper.v1_1_8;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.blocktyper.v1_1_8.recipes.IRecipe;

public interface IBlockTyperPlugin extends Plugin, IBlockTyperUtility {

	void onPrepareItemCraft(PrepareItemCraftEvent event);

	void onCraftItem(CraftItemEvent event);
	
	IRecipe bootstrapRecipe(IRecipe recipe);

	String getRecipeKey(ItemStack item);
	
	<T extends BlockTyperListener> BlockTyperListener registerListener(Class<T> type);
	
	<T extends BlockTyperCommand> BlockTyperCommand registerCommand(String commandName, Class<T> type);

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
