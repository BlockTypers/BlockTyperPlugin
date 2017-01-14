package com.blocktyper.recipes;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public interface IRecipe {

	public static final String NBT_BLOCKTYPER_RECIPE_KEY = "BLOCKTYPER_RECIPE_KEY";
	public static final String NBT_BLOCKTYPER_NAME_LOCALE = "BLOCKTYPER_NAME_LOCALE";
	public static final String NBT_BLOCKTYPER_LORE_LOCALE = "BLOCKTYPER_LORE_LOCALE";
	public static final String NBT_BLOCKTYPER_UNIQUE_ID = "BLOCKTYPER_UNIQUE_ID";
	public static final String HIDDEN_RECIPE_KEY = "HIDDEN_RECIPE_KEY:";
	public static final String INVIS_LORE_PREFIX = "LORE#:";

	void registerRecipe();

	String getKey();

	String getName();

	List<String> getLore();
	
	List<String> getInitialLore();
	
	Map<String, List<String>> getLocaleInitialLoreMap();

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
