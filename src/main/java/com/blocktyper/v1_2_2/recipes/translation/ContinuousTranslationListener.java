package com.blocktyper.v1_2_2.recipes.translation;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_2.BlockTyperListener;
import com.blocktyper.v1_2_2.IBlockTyperPlugin;
import com.blocktyper.v1_2_2.nbt.NBTItem;
import com.blocktyper.v1_2_2.recipes.IRecipe;
import com.blocktyper.v1_2_2.recipes.RecipeRegistrar;

public abstract class ContinuousTranslationListener extends BlockTyperListener {

	public ContinuousTranslationListener(IBlockTyperPlugin plugin) {
		super();
		init(plugin);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected boolean continuousTranslationEnabled() {
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
