package com.blocktyper.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.Listener;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.plugin.BlockTyperPlugin;
import com.blocktyper.plugin.IBlockTyperPlugin;

public class BlockTyperRecipeRegistrar implements IBlockTyperRecipeRegistrar {

	public static String RECIPES_KEY = "recipes";
	public static String RECIPE_KEY = "recipe";

	public static String RECIPE_PROPERTY_SUFFIX_NAME = ".name";
	public static String RECIPE_PROPERTY_SUFFIX_ROWS = ".rows";
	public static String RECIPE_PROPERTY_SUFFIX_OUTPUT = ".output";
	public static String RECIPE_PROPERTY_SUFFIX_MATS = ".mats";
	public static String RECIPE_PROPERTY_SUFFIX_ITEM_STARTS_WITH = ".item.starts.with";
	public static String RECIPE_PROPERTY_SUFFIX_KEEP = ".keep";
	public static String RECIPE_PROPERTY_SUFFIX_LISTENERS = ".listeners";
	
	
	public static String LOCALIZED_KEY_LOADING_RECIPES = "block.typer.loading.recipes";
	
	protected IBlockTyperPlugin plugin;
	protected BlockTyperConfig config;
	protected Map<Integer, List<IRecipe>> materialMatrixHashToRecipesListMap;
	protected Map<String, IRecipe> recipeMap; 

	public BlockTyperRecipeRegistrar(IBlockTyperPlugin plugin) {
		materialMatrixHashToRecipesListMap = new HashMap<Integer, List<IRecipe>>();
		this.recipeMap = new HashMap<String, IRecipe>();
		this.plugin = plugin;
		this.config = plugin.config();
	}
	
	public IRecipe getRecipeFromKey(String key){
		return recipeMap.containsKey(key) ? recipeMap.get(key) : null;
	}

	public void registerRecipesFromConfig() {
		if (config.recipesDisabled()) {
			plugin.info("recipes are disabled");
			return;
		}
		String localizedMessage = plugin.getLocalizedMessage(LOCALIZED_KEY_LOADING_RECIPES);
		plugin.info(localizedMessage);

		//register the crafting listener.  It will be responsible for making sure things are 
		//named correctly before allowing the item to be crafted
		BlockTyperRecipeCraftingListener recipeCraftingListener = new BlockTyperRecipeCraftingListener(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(recipeCraftingListener, plugin);

		
		//This holds all recipes in the config file that will we will try to register
		List<String> configuredReciped = config.getConfig().getStringList(RECIPES_KEY);
		configuredReciped = configuredReciped == null ? new ArrayList<String>() : configuredReciped;

		if (configuredReciped == null || configuredReciped.isEmpty()) {
			plugin.warning("no recipes are configured");
			return;
		}

		int recipesRegistered = 0;
		int variantsRegisted = 0;
		
		
		
		for (String recipe : configuredReciped) {

			String recipeKeyRoot = RECIPE_KEY + "." + recipe;

			if(plugin.config().logRecipes()){
				plugin.section(false);
				plugin.section(false, BlockTyperPlugin.HASHES);
				plugin.info("loading recipe: " + recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_NAME);
			}
			String recipeName = config.getConfig().getString(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_NAME);

			//Name of the recipe (will be used for the Display name of the ItemMeta)
			if (recipeName == null || recipeName.trim().isEmpty()) {
				if(plugin.config().logRecipes())
					plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_NAME + "' provided");
				continue;
			}else{
				if(plugin.config().logRecipes())
					plugin.info("name: " + recipeName);
			}

			//This is the result material type of the crafted item
			String recipeOutput = config.getConfig().getString(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_OUTPUT);
			if (recipeOutput == null || recipeOutput.trim().isEmpty()) {
				if(plugin.config().logRecipes())
					plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_OUTPUT + "' provided");
				continue;
			}

			//This will validate the output Material
			Material outputMaterial = Material.matchMaterial(recipeOutput);
			if (outputMaterial == null) {
				if(plugin.config().logRecipes())
					plugin.info("'" + RECIPE_PROPERTY_SUFFIX_OUTPUT + "' not recognized: " + recipeOutput);
				continue;
			}

			//each recipe need the rows defined
			//each row represents a 3 column row of the crafting inventory
			// - DCC
			// - CSC
			// - CCC
			List<String> recipeRows = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_ROWS);
			if (recipeRows == null || recipeRows.isEmpty()) {
				if(plugin.config().logRecipes())
					plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_ROWS + "' provided");
				continue;
			}

			//This is a list of entries in the form key=value 
			//The key is a letter from the recipeRows and the value is the Material that it represents
			// -D=DIAMOND
			// -C=COMPASS
			// -S=DIAMOND_SWORD
			List<String> recipeMats = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_MATS);
			if (recipeMats == null || recipeMats.isEmpty()) {
				if(plugin.config().logRecipes())
					plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_MATS + "' provided");
				continue;
			}


			//this is not required.  It can be used in BlockTyperRecipeCraftListener's method
			//listening for the PrepareItemCraftEvent.  It will require that the crafting inventory
			//contains items in specific locations which start with a given display name in order
			//for the item to be successfully crafted
			// -0=Mystical
			// -4=Sword of Fury
			List<String> itemStartsWithMatrix = config.getConfig()
					.getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_ITEM_STARTS_WITH);

			
			
			List<String> listenersList = config.getConfig()
					.getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_LISTENERS);

			
			//Build a map of Characters to Materials so we can validate that the
			//user has supplied correct materials and so we can build the correct
			//recipe to register with the server
			Map<Character, Material> materialMap = new HashMap<Character, Material>();
			int rowNumber = 0;
			if(plugin.config().logRecipes())
				plugin.info("parsing mats: ");
			for (String letterEqualsExpression : recipeMats) {
				if(plugin.config().logRecipes())
					plugin.info("  -" + letterEqualsExpression);

				if (!letterEqualsExpression.contains("=") || letterEqualsExpression.indexOf("=") == 0
						|| ((letterEqualsExpression.indexOf("=") + 1) == letterEqualsExpression.length())) {
					if(plugin.config().logRecipes())
						plugin.warning("not a valid letterEqualsExpression");
					continue;
				}

				String letter = letterEqualsExpression.substring(0, letterEqualsExpression.indexOf("="));

				if (letter == null || letter.isEmpty()) {
					if(plugin.config().logRecipes())
						plugin.warning("letter was null or empty");
				}

				if (materialMap.containsKey(letter.charAt(0))) {
					plugin.debugInfo("materialMap containsKey '" + letter.charAt(0) + "'");
					continue;
				}

				String materialString = letterEqualsExpression.substring(letterEqualsExpression.indexOf("=") + 1);

				if (materialString == null || materialString.isEmpty()) {
					if(plugin.config().logRecipes())
						plugin.warning("materialString was null or empty");
					continue;
				}

				Material material = Material.getMaterial(materialString);

				if (material == null) {
					plugin.debugWarning("material not recognized");
					continue;
				}

				if(plugin.config().logRecipes())
					plugin.debugInfo("materialMap.put(" + letter.charAt(0) + ", " + material + ");");

				if(material == Material.AIR){
					material = null;
				}
				
				materialMap.put(letter.charAt(0), material);

			}
			
			
			//This will be used to cache this exact pattern of Materials
			//We will not register this pattern more than once, but we will store the the
			//first one like it as well as all variants in the materialMatrixHashToRecipesListMap
			//The materialMatrixHashToRecipesListMap will. be used to supply the BlockTyperCraftingListener
			//With all configured recipes.  The BlockTyperCraftingListener will determine when the items in the
			//crafting inventory match at least one of the configured recipes for that Material shape
			//Which ever configured recipe matches first will win, so the order that they appear in the recipes list
			//is important if you want to overload recipe material shapes
			
			if(plugin.config().logRecipes()){
				plugin.debugInfo("loading materialMatrix");
				plugin.info("parsing pattern: ");
			}
			List<Material> materialMatrix = new ArrayList<Material>();
			rowNumber = 0;
			for (String row : recipeRows) {
				if(plugin.config().logRecipes())
					plugin.info("  -" + row);
				for(int i = 0; i < 3; i++){
					Material mat = row.length() > i ? (materialMap.get(row.charAt(i)) == null ? Material.AIR : materialMap.get(row.charAt(i))) : Material.AIR;
					materialMatrix.add((rowNumber*3) + i, mat);
				}
				rowNumber++;
			}
			
			
			//this is not required.  It can be used in BlockTyperRecipeCraftListener's method
			//listening for the CraftItemEvent.  It will allow users to keep items in their 
			//crafting inventories
			// - NNN
			// - NYN
			// - NNN
			List<String> recipeKeepMatrix = config.getConfig()
					.getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_KEEP);

			if (recipeKeepMatrix != null && plugin.config().logRecipes()) {
				plugin.info("Keep Rows: ");
				for (String keepRow : recipeKeepMatrix) {
					plugin.info("  -" + keepRow);
				}
			}
			
			
			//Once data is loaded create the recipe and register it
			IRecipe recipeObj = new BlockTyperRecipe(recipeName, recipeKeyRoot, outputMaterial, materialMatrix,
					itemStartsWithMatrix, recipeKeepMatrix, plugin);
			
			plugin.debugInfo("added recipe to map: " + recipe);
			recipeMap.put(recipe, recipeObj);
			
			
			if (listenersList != null) {
				plugin.debugInfo("   -registering listeners: ");
				for (String listenerClassName : listenersList) {
					
					plugin.debugInfo(listenerClassName);
					
					Listener listener = null;
					
					try {
						listener = (Listener)Class.forName(listenerClassName).newInstance();
						
						if(listener != null){
							plugin.getServer().getPluginManager().registerEvents(listener, plugin);
							
							if(plugin.config().logRecipes()){
								plugin.info("listener registered: ");
								plugin.info(listenerClassName);
							}
						}
						
					} catch (InstantiationException e) {
						plugin.debugWarning(e.getMessage());
					} catch (IllegalAccessException e) {
						plugin.debugWarning(e.getMessage());
					} catch (ClassNotFoundException e) {
						plugin.debugWarning(e.getMessage());
					}
				}
			}

			if (materialMatrixHashToRecipesListMap.get(recipeObj.getMaterialMatrixHash()) == null) {
				//only register this material shape the first time it is found
				recipeObj.registerRecipe();
				materialMatrixHashToRecipesListMap.put(recipeObj.getMaterialMatrixHash(), new ArrayList<IRecipe>());
				recipesRegistered++;
			} else {
				//we do not register the material shape more than once.
				variantsRegisted++;
			}

			//always store the recipe in the materialMatrixHashToRecipesListMap for use in the BlockTyperCraftingListener 
			materialMatrixHashToRecipesListMap.get(recipeObj.getMaterialMatrixHash()).add(recipeObj);
			
			if(plugin.config().logRecipes()){
				plugin.info("recipe registered :" + recipeObj.getName() + " [" + recipeObj.getKey() + "]");
				plugin.section(false, BlockTyperPlugin.HASHES);
			}
		}
		
		
		plugin.info("recipes registered:" + recipesRegistered, BlockTyperPlugin.DASHES_TOP);
		plugin.info("variants registered:" + variantsRegisted);
		
	}

	public List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash) {
		return materialMatrixHashToRecipesListMap != null ? materialMatrixHashToRecipesListMap.get(materialMatrixHash)
				: null;
	}

}
