package com.blocktyper.v1_2_1.recipes;

import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public interface IBlockTyperRecipeRegistrar {
	void registerRecipesFromConfig();

	void registerRecipe(IRecipe recipe);

	List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash);

	IRecipe getRecipeFromKey(String key);

	List<IRecipe> getRecipes();
	
	String getInvisibleLorePrefix();

	List<String> getLocalizedLore(IRecipe recipe, HumanEntity player);

	String getLocalizedName(IRecipe recipe, HumanEntity player);

	List<String> getLoreConsiderLocalization(IRecipe recipe, HumanEntity player);

	String getNameConsiderLocalization(IRecipe recipe, HumanEntity player);

	ItemStack getItemFromRecipe(String recipeKey, HumanEntity player, ItemStack baseItem, Integer stackSize);

	ItemStack getItemFromRecipe(IRecipe recipe, HumanEntity player, ItemStack baseItem, Integer stackSize);
	
	ItemStack getItemFromRecipe(IRecipe recipe, HumanEntity player, ItemStack baseItem, Integer stackSize, boolean isIntial);

}
