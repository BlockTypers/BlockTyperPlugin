package com.blocktyper.recipes;

import java.util.List;

import org.bukkit.Material;

public interface IRecipe {
	void registerRecipe();

	String getKey();

	String getName();

	int getMaterialMatrixHash();

	Material getOutput();

	List<Material> getMaterialMatrix();

	List<String> getItemStartsWithMatrix();

	List<String> getKeepsMatrix();
}
