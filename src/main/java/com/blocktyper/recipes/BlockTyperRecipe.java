package com.blocktyper.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class BlockTyperRecipe implements IRecipe {
	private String name;
	private List<String> lore;
	private String key;
	private int materialMatrixHash;

	private Material output;
	private int amount;
	private List<Material> materialMatrix;
	private Map<Integer, String> itemStartsWithMatrix;
	private Map<Integer, String> itemHasHiddenKeyMatrix;
	private List<String> keepsMatrix;
	private List<String> listeners;

	private boolean opOnly;

	private List<String> locales;
	private Map<String, String> localeNameMap;
	private Map<String, List<String>> localeLoreMap;

	private List<Integer> transferSourceLoreMatrix;
	private List<Integer> transferSourceEnchantmentMatrix;
	private Integer transferSourceNameSlot;

	public static String EMPTY_CHARACTER = " ";

	public IBlockTyperPlugin plugin;

	private static final String HIDDEN_RECIPE_KEY = "HIDDEN_RECIPE_KEY:";

	public static String getRecipeKeyToBeHidden(String recipeKey) {
		return HIDDEN_RECIPE_KEY + recipeKey;
	}

	public static boolean isHiddenRecipeKey(String s) {
		return s != null && s.startsWith(HIDDEN_RECIPE_KEY);
	}

	public BlockTyperRecipe(String key, List<Material> materialMatrix, Material output, IBlockTyperPlugin plugin) {
		super();
		this.key = key;
		this.materialMatrix = materialMatrix;
		this.output = output;
		this.plugin = plugin;
		this.name = null;
		this.amount = 1;
		this.itemStartsWithMatrix = null;
		this.keepsMatrix = null;
		this.opOnly = false;
		this.localeNameMap = new HashMap<>();
		this.localeLoreMap = new HashMap<>();

		Integer materialMatrixHashTemp = initMaterialMatrixHash(materialMatrix);

		if (materialMatrixHashTemp == null || materialMatrixHashTemp == 0) {
			throw new IllegalArgumentException("materialMatrix not set");
		}

		materialMatrixHash = materialMatrixHashTemp;
	}

	public void registerRecipe() {
		Recipe recipe = null;
		if (materialMatrix != null && !materialMatrix.isEmpty()) {

			ItemStack outputItem = new ItemStack(output);

			if (materialMatrix.size() == 1) {
				recipe = new FurnaceRecipe(outputItem, materialMatrix.get(0));
			} else {
				ShapedRecipe shapedRecipe = new ShapedRecipe(outputItem);

				String topRowString = "";
				String middleRowString = "";
				String bottomRowString = "";

				Map<Character, Material> charToMatMap = new HashMap<Character, Material>();

				int i = 65;
				for (Material material : materialMatrix) {
					Character character = (char) i;
					if (material != null) {
						charToMatMap.put(character, material);
					}

					if (topRowString == null || topRowString.length() < 3) {
						topRowString += material != null && !material.equals(Material.AIR) ? character
								: EMPTY_CHARACTER;
					} else if (middleRowString == null || middleRowString.length() < 3) {
						middleRowString += material != null && !material.equals(Material.AIR) ? character
								: EMPTY_CHARACTER;
					} else if (bottomRowString == null || bottomRowString.length() < 3) {
						bottomRowString += material != null && !material.equals(Material.AIR) ? character
								: EMPTY_CHARACTER;
					}
					i++;
				}

				plugin.debugInfo("[" + topRowString + "]");
				plugin.debugInfo("[" + middleRowString + "]");
				plugin.debugInfo("[" + bottomRowString + "]");

				shapedRecipe.shape(topRowString, middleRowString, bottomRowString);

				plugin.debugInfo("loading character to material map");
				for (Character character : charToMatMap.keySet()) {
					Material mat = charToMatMap.get(character);

					if (mat == null || mat.equals(Material.AIR)) {
						plugin.debugInfo(" -skipped: " + character + " -> " + (mat == null ? "null" : mat.name()));
						continue;
					}

					plugin.debugInfo(" -mapped : " + character + " -> " + (mat == null ? "null" : mat.name()));

					shapedRecipe.setIngredient(character, charToMatMap.get(character));
				}

				recipe = shapedRecipe;
			}

			plugin.getServer().addRecipe(recipe);
		}
	}

	public static Integer initMaterialMatrixHash(List<Material> materialMatrix) {

		if (materialMatrix == null || materialMatrix.isEmpty()) {
			return null;
		}

		final int prime = 31;
		int result = 1;

		int i = 0;
		for (Material material : materialMatrix) {
			i++;
			result = prime * result + ((material == null) ? 1 : material.hashCode());
		}

		if (i == 0) {
			// nothing hashed
			return null;
		}

		return result;
	}

	public static String getEMPTY_CHARACTER() {
		return EMPTY_CHARACTER;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getMaterialMatrixHash() {
		return materialMatrixHash;
	}

	public void setMaterialMatrixHash(int materialMatrixHash) {
		this.materialMatrixHash = materialMatrixHash;
	}

	public Material getOutput() {
		return output;
	}

	public void setOutput(Material output) {
		this.output = output;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public List<Material> getMaterialMatrix() {
		return materialMatrix;
	}

	public void setMaterialMatrix(List<Material> materialMatrix) {
		this.materialMatrix = materialMatrix;
	}

	public Map<Integer, String> getItemStartsWithMatrix() {
		return itemStartsWithMatrix;
	}

	public void setItemStartsWithMatrix(Map<Integer, String> itemStartsWithMatrix) {
		this.itemStartsWithMatrix = itemStartsWithMatrix;
	}

	public List<String> getKeepsMatrix() {
		return keepsMatrix;
	}

	public void setKeepsMatrix(List<String> keepsMatrix) {
		this.keepsMatrix = keepsMatrix;
	}

	public List<String> getListeners() {
		return listeners;
	}

	public void setListeners(List<String> listeners) {
		this.listeners = listeners;
	}

	public boolean isOpOnly() {
		return opOnly;
	}

	public void setOpOnly(boolean opOnly) {
		this.opOnly = opOnly;
	}

	public IBlockTyperPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

	public Map<String, String> getLocaleNameMap() {
		return localeNameMap;
	}

	public void setLocaleNameMap(Map<String, String> localeNameMap) {
		this.localeNameMap = localeNameMap;
	}

	public Map<String, List<String>> getLocaleLoreMap() {
		return localeLoreMap;
	}

	public void setLocaleLoreMap(Map<String, List<String>> localeLoreMap) {
		this.localeLoreMap = localeLoreMap;
	}

	public Map<Integer, String> getItemHasHiddenKeyMatrix() {
		return itemHasHiddenKeyMatrix;
	}

	public void setItemHasHiddenKeyMatrix(Map<Integer, String> itemHasHiddenKeyMatrix) {
		this.itemHasHiddenKeyMatrix = itemHasHiddenKeyMatrix;
	}

	public List<Integer> getTransferSourceLoreMatrix() {
		return transferSourceLoreMatrix;
	}

	public void setTransferSourceLoreMatrix(List<Integer> transferSourceLoreMatrix) {
		this.transferSourceLoreMatrix = transferSourceLoreMatrix;
	}

	public List<Integer> getTransferSourceEnchantmentMatrix() {
		return transferSourceEnchantmentMatrix;
	}

	public void setTransferSourceEnchantmentMatrix(List<Integer> transferSourceEnchantmentMatrix) {
		this.transferSourceEnchantmentMatrix = transferSourceEnchantmentMatrix;
	}

	public Integer getTransferSourceNameSlot() {
		return transferSourceNameSlot;
	}

	public void setTransferSourceNameSlot(Integer transferSourceNameSlot) {
		this.transferSourceNameSlot = transferSourceNameSlot;
	}
	// GETTERS AND SETTERS

}
