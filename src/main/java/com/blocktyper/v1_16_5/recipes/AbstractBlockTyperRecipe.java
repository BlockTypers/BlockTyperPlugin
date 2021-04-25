package com.blocktyper.v1_16_5.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import com.blocktyper.v1_16_5.IBlockTyperPlugin;
import com.blocktyper.v1_16_5.helpers.InvisHelper;

public abstract class AbstractBlockTyperRecipe implements IRecipe {
	private String name;
	private List<String> lore;
	private List<String> initialLore;
	private String key;
	private int materialMatrixHash;

	private Material output;
	private Byte outputData;
	private int amount;
	private List<Material> materialMatrix;
	private List<Byte> materialDataMatrix;
	private Map<String, String> nbtStringData;
	private Map<String, Object> nbtObjectData;
	private Map<Integer, String> itemHasNbtKeyMatrix;
	private Map<Integer, String> itemHasNameTagKeyMatrix;
	private List<String> keepsMatrix;
	private List<String> listeners;

	private boolean opOnly;
	private boolean isNonStacking;

	private List<String> locales;
	private Map<String, String> localeNameMap;
	private Map<String, List<String>> localeLoreMap;
	private Map<String, List<String>> localeInitialLoreMap;

	private List<Integer> transferSourceLoreMatrix;
	private List<Integer> transferSourceEnchantmentMatrix;
	private Integer transferSourceNameSlot;

	public static String EMPTY_CHARACTER = " ";

	protected IBlockTyperPlugin plugin;

	public static String getRecipeKeyToBeHidden(String recipeKey) {
		return HIDDEN_RECIPE_KEY + recipeKey;
	}

	public static boolean isHiddenRecipeKey(String s) {
		return s != null && s.startsWith(HIDDEN_RECIPE_KEY);
	}

	public static String getKeyFromLoreLine(String s) {
		String visibleLore = InvisHelper.convertToVisibleString(s);
		if (visibleLore == null || !visibleLore.contains(HIDDEN_RECIPE_KEY))
			return null;

		String key = visibleLore.substring(visibleLore.indexOf(HIDDEN_RECIPE_KEY) + HIDDEN_RECIPE_KEY.length());

		return key;
	}
	
	public AbstractBlockTyperRecipe(IRecipe recipe, IBlockTyperPlugin plugin){
		super();
		
		this.plugin = plugin;
		
		this.name = recipe.getName();
		this.lore = recipe.getLore();
		this.initialLore = recipe.getInitialLore();
		this.key = recipe.getKey();
		this.materialMatrixHash = recipe.getMaterialMatrixHash();

		this.output = recipe.getOutput();
		this.outputData = recipe.getOutputData();
		this.amount = recipe.getAmount();
		this.materialMatrix = recipe.getMaterialMatrix();
		this.materialDataMatrix = recipe.getMaterialDataMatrix();
		this.nbtStringData = recipe.getNbtStringData();
		this.nbtObjectData = recipe.getNbtObjectData();
		this.itemHasNbtKeyMatrix = recipe.getItemHasNbtKeyMatrix();
		this.itemHasNameTagKeyMatrix = recipe.getItemHasNameTagKeyMatrix();
		this.keepsMatrix = recipe.getKeepsMatrix();
		this.listeners = recipe.getListeners();

		this.opOnly = recipe.isOpOnly();
		this.isNonStacking = recipe.isNonStacking();

		this.locales = recipe.getLocales();
		this.localeNameMap = recipe.getLocaleNameMap();
		this.localeLoreMap = recipe.getLocaleLoreMap();
		this.localeInitialLoreMap = recipe.getLocaleInitialLoreMap();

		this.transferSourceLoreMatrix = recipe.getTransferSourceLoreMatrix();
		this.transferSourceEnchantmentMatrix = recipe.getTransferSourceEnchantmentMatrix();
		this.transferSourceNameSlot = recipe.getTransferSourceNameSlot();
		

	}

	public AbstractBlockTyperRecipe(String key, List<Material> materialMatrix, List<Byte> materialDataMatrix, Material output, IBlockTyperPlugin plugin) {
		super();
		init(key, materialMatrix, materialDataMatrix, output, plugin);
	}
	
	private void init(String key, List<Material> materialMatrix, List<Byte> materialDataMatrix, Material output, IBlockTyperPlugin plugin){
		this.key = key;
		this.materialMatrix = materialMatrix;
		this.materialDataMatrix = materialDataMatrix;
		this.output = output;
		this.plugin = plugin;
		this.name = null;
		this.amount = 1;
		this.keepsMatrix = null;
		this.opOnly = false;
		this.localeNameMap = new HashMap<>();
		this.localeLoreMap = new HashMap<>();
		this.localeInitialLoreMap = new HashMap<>();

		Integer materialMatrixHashTemp = initMaterialMatrixHash(materialMatrix, materialDataMatrix);

		if (materialMatrixHashTemp == null || materialMatrixHashTemp == 0) {
			throw new IllegalArgumentException("materialMatrix not set");
		}

		materialMatrixHash = materialMatrixHashTemp;
	}

	@SuppressWarnings("deprecation")
	public void registerRecipe() {
		Recipe recipe = null;
		if (materialMatrix != null && !materialMatrix.isEmpty()) {

			ItemStack outputItem = new ItemStack(output);
			
			if(outputData != null){
				outputItem = new ItemStack(output, 1, outputItem.getDurability(), outputData);
			}

			if (materialMatrix.size() == 1) {
				recipe = new FurnaceRecipe(outputItem, materialMatrix.get(0));
			} else {
				ShapedRecipe shapedRecipe = new ShapedRecipe(outputItem);

				String topRowString = "";
				String middleRowString = "";
				String bottomRowString = "";

				Map<Character, Material> charToMatMap = new HashMap<Character, Material>();
				Map<Character, Byte> charToMatDataMap = new HashMap<Character, Byte>();

				int asciiValue = 65;
				int index = 0;
				for (Material material : materialMatrix) {
					Character character = (char) (index+asciiValue);
					if (material != null) {
						charToMatMap.put(character, material);
					}
					Byte data = 0;
					if(materialDataMatrix != null && materialDataMatrix.get(index) != null){
						data = materialDataMatrix.get(index);
					}
					charToMatDataMap.put(character, data);

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
					index++;
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
					
					shapedRecipe.setIngredient(character, new MaterialData(charToMatMap.get(character), charToMatDataMap.get(character)));
				}

				recipe = shapedRecipe;
			}

			plugin.getServer().addRecipe(recipe);
		}
	}

	public static Integer initMaterialMatrixHash(List<Material> materialMatrix, List<Byte> materialDataMatrix) {

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
		
		if(materialDataMatrix != null){
			for (Byte data : materialDataMatrix) {
				int byteAsInt = data == null ? 0 : data.intValue();
				result = result + prime * result * byteAsInt;
			}
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

	public Map<String, List<String>> getLocaleInitialLoreMap() {
		return localeInitialLoreMap;
	}

	public void setLocaleInitialLoreMap(Map<String, List<String>> localeInitialLoreMap) {
		this.localeInitialLoreMap = localeInitialLoreMap;
	}
	
	public List<String> getInitialLore() {
		return initialLore;
	}

	public void setInitialLore(List<String> initialLore) {
		this.initialLore = initialLore;
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

	public List<Byte> getMaterialDataMatrix() {
		return materialDataMatrix;
	}

	public void setMaterialDataMatrix(List<Byte> materialDataMatrix) {
		this.materialDataMatrix = materialDataMatrix;
	}

	public Material getOutput() {
		return output;
	}

	public void setOutput(Material output) {
		this.output = output;
	}

	public Byte getOutputData() {
		return outputData;
	}

	public void setOutputData(Byte outputData) {
		this.outputData = outputData;
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

	public boolean isNonStacking() {
		return isNonStacking;
	}

	public void setNonStacking(boolean isNonStacking) {
		this.isNonStacking = isNonStacking;
	}

	public IBlockTyperPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	public Map<Integer, String> getItemHasNbtKeyMatrix() {
		return itemHasNbtKeyMatrix;
	}

	public void setItemHasNbtKeyMatrix(Map<Integer, String> itemHasNbtKeyMatrix) {
		this.itemHasNbtKeyMatrix = itemHasNbtKeyMatrix;
	}

	public Map<Integer, String> getItemHasNameTagKeyMatrix() {
		return itemHasNameTagKeyMatrix;
	}

	public void setItemHasNameTagKeyMatrix(Map<Integer, String> itemHasNameTagKeyMatrix) {
		this.itemHasNameTagKeyMatrix = itemHasNameTagKeyMatrix;
	}

	public Map<String, String> getNbtStringData() {
		return nbtStringData;
	}

	public void setNbtStringData(Map<String, String> nbtStringData) {
		this.nbtStringData = nbtStringData;
	}

	public Map<String, Object> getNbtObjectData() {
		return nbtObjectData;
	}

	public void setNbtObjectData(Map<String, Object> nbtObjectData) {
		this.nbtObjectData = nbtObjectData;
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
