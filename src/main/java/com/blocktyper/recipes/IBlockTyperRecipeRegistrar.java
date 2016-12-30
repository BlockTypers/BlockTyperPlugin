package com.blocktyper.recipes;

import java.util.List;

public interface IBlockTyperRecipeRegistrar {
	void registerRecipesFromConfig();
	List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash);
	IRecipe getRecipeFromKey(String key);
	List<IRecipe> getRecipes();

}
