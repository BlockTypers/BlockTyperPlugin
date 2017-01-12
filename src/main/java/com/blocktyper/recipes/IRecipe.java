package com.blocktyper.recipes;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public interface IRecipe {
	void registerRecipe();

	String getKey();

	String getName();

	List<String> getLore();
	
	List<String> getLocales();
	
	Map<String, String> getNbtStringData();
	
	Map<String, String> getLocaleNameMap();
	
	Map<String, List<String>> getLocaleLoreMap();

	int getMaterialMatrixHash();

	Material getOutput();
	
	boolean isOpOnly();
	
	boolean isNonStacking();
	
	int getAmount();

	List<Material> getMaterialMatrix();
	
	Map<Integer, String> getItemHasNbtKeyMatrix();

	List<String> getKeepsMatrix();
	
	List<String> getListeners();
	
	List<Integer> getTransferSourceLoreMatrix();
	
	List<Integer> getTransferSourceEnchantmentMatrix();
	
	Integer getTransferSourceNameSlot();
	
}
