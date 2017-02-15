package com.blocktyper.v1_2_2;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_2.nbt.NBTItem;
import com.blocktyper.v1_2_2.plugin.BlockTyperJsonFileWriterPlugin;
import com.blocktyper.v1_2_2.recipes.IBlockTyperRecipeRegistrar;
import com.blocktyper.v1_2_2.recipes.RecipeRegistrar;
import com.blocktyper.v1_2_2.recipes.translation.TranslateOnInventoryClickListener;
import com.blocktyper.v1_2_2.recipes.translation.TranslateOnInventoryOpenListener;
import com.blocktyper.v1_2_2.recipes.translation.TranslateOnPickupListener;

public abstract class BlockTyperBasePlugin extends BlockTyperJsonFileWriterPlugin {

	protected IBlockTyperRecipeRegistrar recipeRegistrar;

	protected boolean useOnPickupTranslationListener = true;
	protected boolean useOnInventoryOpenTranslationListener = true;
	protected boolean useOnInventoryClickTranslationListener = true;

	@Override
	public IBlockTyperRecipeRegistrar recipeRegistrar() {
		return recipeRegistrar;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		if (getRecipesNbtKey() != null) {
			recipeRegistrar = new RecipeRegistrar(this);
			recipeRegistrar.registerRecipesFromConfig();

			if (getConfig().getBoolean(RecipeRegistrar.RECIPES_CONTINUOUS_TRANSLATION_KEY, false)) {
				if (useOnInventoryClickTranslationListener) {
					new TranslateOnInventoryClickListener(this);
				}
				if (useOnInventoryOpenTranslationListener) {
					new TranslateOnInventoryOpenListener(this);
				}
				if (useOnPickupTranslationListener) {
					new TranslateOnPickupListener(this);
				}
			}
		}
	}

	@Override
	public String getRecipeKey(ItemStack item) {
		if (item != null) {
			NBTItem nbtItem = new NBTItem(item);
			if (nbtItem.hasKey(getRecipesNbtKey())) {
				String recipeKey = nbtItem.getString(getRecipesNbtKey());
				return recipeKey;
			}
		}
		return null;
	}

	@Override
	public <T> T getObject(ItemStack item, String key, Class<T> type) {
		if (item == null) {
			return null;
		}

		NBTItem nbtItem = new NBTItem(item);
		T outObject = nbtItem.getObject(key, type);
		return outObject;
	}

	//////////////////
	// RECIPE HOOKS///
	/////////////////
	@Override
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {

	}

	@Override
	public void onCraftItem(CraftItemEvent event) {

	}
}
