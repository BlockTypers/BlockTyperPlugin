package com.blocktyper.v1_1_8.recipes.translation;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_1_8.nbt.NBTItem;
import com.blocktyper.v1_1_8.plugin.IBlockTyperPlugin;
import com.blocktyper.v1_1_8.recipes.IRecipe;
import com.blocktyper.v1_1_8.recipes.RecipeRegistrar;

public abstract class ContinuousTranslationListener implements Listener {

	protected IBlockTyperPlugin plugin;

	public ContinuousTranslationListener(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	protected boolean continuousTranslationEnabled(){
		return plugin.getConfig().getBoolean(RecipeRegistrar.RECIPES_CONTINUOUS_TRANSLATION_KEY, false);
	}

	protected ItemStack convertItemStackLanguage(ItemStack itemStack, HumanEntity player) {

		String recipeKey = new NBTItem(itemStack).getString(plugin.getRecipesNbtKey());

		if (recipeKey == null)
			return itemStack;

		IRecipe recipe = plugin.recipeRegistrar().getRecipeFromKey(recipeKey);

		if (recipe == null)
			return itemStack;

		plugin.debugInfo("Translating: " + itemStack.getType().name());

		return plugin.recipeRegistrar().getItemFromRecipe(recipe, player, itemStack, itemStack.getAmount(), false);
	}
}
