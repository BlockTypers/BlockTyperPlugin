package com.blocktyper.v1_1_8.recipes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import com.blocktyper.v1_1_8.IBlockTyperPlugin;

class BlockTyperRecipe extends AbstractBlockTyperRecipe{

	BlockTyperRecipe(String key, List<Material> materialMatrix, List<Byte> materialDataMatrix, Material output,
			IBlockTyperPlugin plugin) {
		super(key, materialMatrix, materialDataMatrix, output, plugin);
	}

	@Override
	public List<String> getLocalizedLoreForPlugin(IRecipe recipe, HumanEntity player) {
		return null;
	}

}
