package com.blocktyper.recipes;

import java.util.List;

import org.bukkit.Material;

import com.blocktyper.plugin.IBlockTyperPlugin;

public interface IBlockTyperRecipeRegistrar {
	void registerRecipesFromConfig();
	List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash);
	IRecipe getRecipeFromKey(String key);
	List<IRecipe> getRecipes();
	void registerRecipe(String recipeKey, String recipeName, List<String> lore, Material outputMaterial, int amount, boolean opOnly, List<Material> materialMatrix,
			List<String> itemStartsWithMatrix, List<String> recipeKeepMatrix, IBlockTyperPlugin plugin, List<String> listenersList);

}
