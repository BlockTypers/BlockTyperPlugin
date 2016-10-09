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
	private String key;
	private int materialMatrixHash;

	private Material output;
	private List<Material> materialMatrix;
	private List<String> itemStartsWithMatrix;
	private List<String> keepsMatrix;
	
	public static String EMPTY_CHARACTER = " ";
	
	IBlockTyperPlugin plugin;
	

	public BlockTyperRecipe(String name, String key, Material output, List<Material> materialMatrix,
			List<String> itemStartsWithMatrix, List<String> keepsMatrix, IBlockTyperPlugin plugin) {
		super();
		this.name = name;
		this.key = key;
		this.output = output;
		this.materialMatrix = materialMatrix;
		this.itemStartsWithMatrix = itemStartsWithMatrix;
		this.keepsMatrix = keepsMatrix;
		this.plugin = plugin;

		plugin.debugInfo("generating materialMatrixHash");
		
		for (Material material : materialMatrix) {

			if(plugin.config().debugEnabled())
				plugin.debugInfo("materialMatrix.add("+(material != null ? material : "null")+")");
		}
		
		Integer materialMatrixHashTemp = initMaterialMatrixHash(materialMatrix);

		if (materialMatrixHashTemp == null || materialMatrixHashTemp == 0) {
			throw new IllegalArgumentException("materialMatrix not set");
		}
		
		materialMatrixHash = materialMatrixHashTemp;
		
		plugin.debugInfo("materialMatrixHash: " + materialMatrixHash);
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
						topRowString += material != null && !material.equals(Material.AIR) ? character : EMPTY_CHARACTER;
					} else if (middleRowString == null || middleRowString.length() < 3) {
						middleRowString += material != null && !material.equals(Material.AIR) ? character : EMPTY_CHARACTER;
					} else if (bottomRowString == null || bottomRowString.length() < 3) {
						bottomRowString += material != null && !material.equals(Material.AIR) ? character : EMPTY_CHARACTER;
					}
					i++;
				}
				
				plugin.debugInfo("[" + topRowString + "]");
				plugin.debugInfo("[" + middleRowString + "]");
				plugin.debugInfo("[" + bottomRowString + "]");
				

				shapedRecipe.shape(topRowString, middleRowString, bottomRowString);
				
				
				plugin.debugInfo("loading character to material map");
				for(Character character : charToMatMap.keySet()){
					Material mat = charToMatMap.get(character);
					
					if(mat == null || mat.equals(Material.AIR)){
						plugin.debugInfo(" -skipped: " + character+" -> " + (mat == null ? "null" : mat.name()));
						continue;
					}
					
					plugin.debugInfo(" -mapped : " + character+" -> " + (mat == null ? "null" : mat.name()));
					
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

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}
	
	

	public Material getOutput() {
		return output;
	}

	public int getMaterialMatrixHash() {
		return materialMatrixHash;
	}

	public List<Material> getMaterialMatrix() {
		return materialMatrix;
	}

	public List<String> getItemStartsWithMatrix() {
		return itemStartsWithMatrix;
	}

	public List<String> getKeepsMatrix() {
		return keepsMatrix;
	}

}
