package com.blocktyper.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.helpers.InvisibleLoreHelper;
import com.blocktyper.nbt.NBTItem;
import com.blocktyper.plugin.BlockTyperPlugin;
import com.blocktyper.plugin.IBlockTyperPlugin;

public class RecipeRegistrar implements IBlockTyperRecipeRegistrar {

	public static String RECIPES_KEY = "recipes";
	public static String RECIPES_WORLDS_KEY = "recipes-worlds";
	public static String RECIPE_KEY = "recipe";
	public static String RECIPES_CONTINUOUS_TRANSLATION_KEY = "recipes-settings.continuous-translation";

	public static String RECIPE_PROPERTY_SUFFIX_NAME = ".name";
	public static String RECIPE_PROPERTY_SUFFIX_LORE = ".lore";
	public static String RECIPE_PROPERTY_SUFFIX_ROWS = ".rows";
	public static String RECIPE_PROPERTY_SUFFIX_OUTPUT = ".output";
	public static String RECIPE_PROPERTY_SUFFIX_AMOUNT = ".amount";
	public static String RECIPE_PROPERTY_SUFFIX_MATS = ".mats";
	public static String RECIPE_PROPERTY_SUFFIX_ITEM_STARTS_WITH = ".item-starts-with";
	public static String RECIPE_PROPERTY_SUFFIX_ITEM_HAS_NBT_KEY = ".item-has-nbt-key";
	public static String RECIPE_PROPERTY_SUFFIX_OP_ONLY = ".op-only";
	public static String RECIPE_PROPERTY_SUFFIX_LOCALES = ".locales";
	public static String RECIPE_PROPERTY_SUFFIX_KEEP = ".keep";
	public static String RECIPE_PROPERTY_SUFFIX_LISTENERS = ".listeners";

	public static String LOCALIZED_KEY_LOADING_RECIPES = "block.typer.loading.recipes";

	protected IBlockTyperPlugin plugin;
	protected BlockTyperConfig config;
	protected Map<Integer, List<IRecipe>> materialMatrixHashToRecipesListMap;
	protected Map<String, IRecipe> recipeMap;
	protected int recipesRegistered = 0;
	protected int variantsRegisted = 0;

	private String nameLocale = null;
	private String loreLocale = null;

	public RecipeRegistrar(IBlockTyperPlugin plugin) {
		materialMatrixHashToRecipesListMap = new HashMap<Integer, List<IRecipe>>();
		this.recipeMap = new HashMap<String, IRecipe>();
		this.plugin = plugin;
		this.config = plugin.config();
	}

	// BEGIN IBlockTyperRecipeRegistrar interface methods
	public IRecipe getRecipeFromKey(String key) {
		return recipeMap.containsKey(key) ? recipeMap.get(key) : null;
	}

	public List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash) {
		return materialMatrixHashToRecipesListMap != null ? materialMatrixHashToRecipesListMap.get(materialMatrixHash)
				: null;
	}

	public List<IRecipe> getRecipes() {
		return recipeMap != null ? new ArrayList<IRecipe>(recipeMap.values()) : null;
	}

	public void registerRecipesFromConfig() {
		if (config.recipesDisabled()) {
			plugin.info("recipes are disabled");
			return;
		}
		String localizedMessage = plugin.getLocalizedMessage(LOCALIZED_KEY_LOADING_RECIPES);
		plugin.info(localizedMessage);

		// register the crafting listener. It will be responsible for making
		// sure things are
		// named correctly before allowing the item to be crafted
		RecipeCraftingListener recipeCraftingListener = new RecipeCraftingListener(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(recipeCraftingListener, plugin);

		// This holds all recipes in the config file that will we will try to
		// register
		List<String> configuredReciped = config.getConfig().getStringList(RECIPES_KEY);
		configuredReciped = configuredReciped == null ? new ArrayList<String>() : configuredReciped;

		if (configuredReciped == null || configuredReciped.isEmpty()) {
			plugin.warning("no recipes are configured");
			return;
		}

		for (String recipeKey : configuredReciped) {
			IRecipe recipe = getRecipeFromConfig(recipeKey);
			if (recipe != null)
				registerRecipe(recipe);
		}

		plugin.info("recipes registered:" + recipesRegistered, BlockTyperPlugin.DASHES_TOP);
		plugin.info("variants registered:" + variantsRegisted);

	}

	public void registerRecipe(IRecipe recipe) {
		if(recipe == null){
			plugin.debugWarning("null recipe passed");
			return;
		}
		
		if(plugin.getRecipesNbtKey() == null){
			plugin.warning("Recipe not registered.  No Recipe NBT tag was set: " + recipe.getKey());
		}
		
		String recipeKey = recipe.getKey();
		recipeMap.put(recipeKey, recipe);

		List<String> listenersList = recipe.getListeners();

		if (listenersList != null) {
			plugin.debugInfo("   -registering listeners: ");
			for (String listenerClassName : listenersList) {
				registerListener(listenerClassName);
			}
		}

		if (materialMatrixHashToRecipesListMap.get(recipe.getMaterialMatrixHash()) == null) {
			// only register this material shape the first time it is found
			recipe.registerRecipe();
			materialMatrixHashToRecipesListMap.put(recipe.getMaterialMatrixHash(), new ArrayList<IRecipe>());
			recipesRegistered++;
		} else {
			// we do not register the material shape more than once.
			variantsRegisted++;
		}

		// always store the recipe in the materialMatrixHashToRecipesListMap for
		// use in the BlockTyperCraftingListener
		materialMatrixHashToRecipesListMap.get(recipe.getMaterialMatrixHash()).add(recipe);

		if (plugin.config().logRecipes()) {
			plugin.info("recipe registered :" + (recipe.getName() != null ? recipe.getName() : "") + " ["
					+ recipe.getKey() + "]" + (recipe.isOpOnly() ? " [OP ONLY]" : ""));
			plugin.section(false, BlockTyperPlugin.HASHES);
		}
	}

	public List<String> getLocalizedLore(IRecipe recipe, HumanEntity player) {
		return getLocalizedLore(player, recipe.getLocaleLoreMap(), true);
	}
	
	public List<String> getLocalizedInitialLore(IRecipe recipe, HumanEntity player) {
		return getLocalizedLore(player, recipe.getLocaleInitialLoreMap(), false);
	}
	
	public List<String> getLocalizedLore(HumanEntity player, Map<String, List<String>> localLoreMap, boolean setLocalLore) {
		if(localLoreMap == null){
			return null;
		}
		
		String localeCode = plugin.getPlayerHelper().getLocale(player);
		List<String> localLore = localLoreMap.get(localeCode);
		if (localLore == null || localLore.isEmpty()) {
			localeCode = plugin.getPlayerHelper().getLanguage(player);
			localLore = localLoreMap.get(localeCode);
		}

		if(setLocalLore){
			loreLocale = localLore != null ? localeCode : null;
		}
		
		return localLore;
	}

	public String getLocalizedName(IRecipe recipe, HumanEntity player) {
		String localeCode = plugin.getPlayerHelper().getLocale(player);
		String localName = recipe.getLocaleNameMap().get(localeCode);
		if (localName == null || localName.isEmpty()) {
			localeCode = plugin.getPlayerHelper().getLanguage(player);
			localName = recipe.getLocaleNameMap().get(localeCode);
		}

		nameLocale = localName != null ? localeCode : null;

		return localName;
	}
	
	
	public List<String> getLoreConsiderLocalization(IRecipe recipe, HumanEntity player) {
		List<String> lore = new ArrayList<>();
		loreLocale = null;
		boolean localeLoreFound = false;
		
		List<String> localeLore = getLocalizedLore(recipe, player);
		if (localeLore != null && !localeLore.isEmpty()) {
			localeLoreFound = true;
			lore.addAll(localeLore);
		}
		
		if (!localeLoreFound && recipe.getLore() != null) {
			lore.addAll(recipe.getLore());
		}
		
		return lore;
	}
	
	private List<String> getInitialLoreConsiderLocalization(IRecipe recipe, HumanEntity player) {
		List<String> initialLore = new ArrayList<>();
		boolean localeLoreFound = false;
		
		List<String> localeInitialLore = getLocalizedInitialLore(recipe, player);
		if (localeInitialLore != null && !localeInitialLore.isEmpty()) {
			localeLoreFound = true;
			initialLore.addAll(localeInitialLore);
		}
		if (!localeLoreFound && recipe.getInitialLore() != null) {
			initialLore.addAll(recipe.getInitialLore());
		}
		
		return initialLore;
	}

	public String getNameConsiderLocalization(IRecipe recipe, HumanEntity player) {
		String name = null;
		nameLocale = null;
		if (recipe.getLocaleNameMap() != null && !recipe.getLocaleNameMap().isEmpty() && player != null) {
			name = getLocalizedName(recipe, player);
		}
		if (name == null || name.isEmpty()) {
			name = recipe.getName();
		}
		return name;
	}

	public ItemStack getItemFromRecipe(String recipeKey, HumanEntity player, ItemStack baseItem, Integer stackSize) {
		IRecipe recipe = getRecipeFromKey(recipeKey);
		return getItemFromRecipe(recipe, player, baseItem, stackSize);

	}
	
	public ItemStack getItemFromRecipe(IRecipe recipe, HumanEntity player, ItemStack baseItem, Integer stackSize) {
		return getItemFromRecipe(recipe, player, baseItem, stackSize, true);
	}
	
	public String getInvisibleLorePrefix(){
		String lorePrefix = IRecipe.INVIS_LORE_PREFIX + plugin.getRecipesNbtKey();
		return lorePrefix;
	}

	public ItemStack getItemFromRecipe(IRecipe recipe, HumanEntity player, ItemStack baseItem, Integer stackSize, boolean isIntial){

		if (recipe == null) {
			plugin.debugWarning("getItemFromRecipe NO MATCH!");
			return null;
		} else {
			plugin.debugInfo("MATCH: " + (recipe.getName() != null ? recipe.getName() : ""));
		}

		Material output = recipe.getOutput();

		ItemStack result = baseItem == null ? new ItemStack(output) : baseItem;

		ItemMeta meta = result.getItemMeta();
		meta = meta == null ? (new ItemStack(result.getType())).getItemMeta() : meta;

		// NAME
		String name = getNameConsiderLocalization(recipe, player);
		meta.setDisplayName(name);

		// LORE
		String lorePrefix = getInvisibleLorePrefix();
		List<String> existingLore = InvisibleLoreHelper.removeLoreWithInvisibleKey(baseItem, player,lorePrefix);
		List<String> lore = getLoreConsiderLocalization(recipe, player);

		if (lore == null)
			lore = new ArrayList<>();

		if (!lore.isEmpty()) {
			final String invisPrefix = InvisibleLoreHelper.convertToInvisibleString(lorePrefix);
			lore = lore.stream().filter(l -> l != null).map(l -> invisPrefix + l).collect(Collectors.toList());
		}
		
		if(isIntial){
			List<String> initialLore = getInitialLoreConsiderLocalization(recipe, player);
			if (initialLore != null){
				lore.addAll(initialLore);
			}
		}
		

		if (existingLore != null){
			lore.addAll(existingLore);
		}

		meta.setLore(lore);
		result.setItemMeta(meta);

		// amount
		if (stackSize != null) {
			if (stackSize > 0) {
				result.setAmount(stackSize);
			} else {
				result.setAmount(output.getMaxStackSize());
			}

		} else {
			result.setAmount(recipe.getAmount());
		}

		NBTItem nbtItem = new NBTItem(result);
		nbtItem.setString(plugin.getRecipesNbtKey(), recipe.getKey());

		if (nameLocale != null){
			nbtItem.setString(IRecipe.NBT_BLOCKTYPER_NAME_LOCALE, nameLocale);
		}

		if (loreLocale != null){
			nbtItem.setString(IRecipe.NBT_BLOCKTYPER_LORE_LOCALE, loreLocale);
		}

		NBTItem baseNbtItem = baseItem == null ? null : new NBTItem(baseItem);
		if (recipe.getNbtStringData() != null && !recipe.getNbtStringData().isEmpty()) {
			for (String key : recipe.getNbtStringData().keySet()) {
				if(baseNbtItem == null || !baseNbtItem.hasKey(key)){
					nbtItem.setString(key, recipe.getNbtStringData().get(key));
				}
			}
		}

		if (recipe.isNonStacking() && !nbtItem.hasKey(IRecipe.NBT_BLOCKTYPER_UNIQUE_ID)) {
			nbtItem.setString(IRecipe.NBT_BLOCKTYPER_UNIQUE_ID, UUID.randomUUID().toString());
		}

		return nbtItem.getItem();
	}
	// END IBlockTyperRecipeRegistrar interface methods

	protected final void registerListener(String listenerClassName) {
		plugin.debugInfo(listenerClassName);

		Listener listener = null;

		try {
			listener = (Listener) Class.forName(listenerClassName).newInstance();

			if (listener != null) {
				plugin.getServer().getPluginManager().registerEvents(listener, plugin);

				if (plugin.config().logRecipes()) {
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

	protected IRecipe getRecipeFromConfig(String recipeKey) {
		String recipeKeyRoot = RECIPE_KEY + "." + recipeKey;

		if (plugin.config().logRecipes()) {
			plugin.section(false);
			plugin.section(false, BlockTyperPlugin.HASHES);
			plugin.info("loading recipe: " + recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_NAME);
		}

		// This is the result material type of the crafted item
		String recipeOutput = config.getConfig().getString(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_OUTPUT);
		if (recipeOutput == null || recipeOutput.trim().isEmpty()) {
			if (plugin.config().logRecipes())
				plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_OUTPUT + "' provided");
			return null;
		}

		// This will validate the output Material
		Material outputMaterial = Material.matchMaterial(recipeOutput);
		if (outputMaterial == null) {
			if (plugin.config().logRecipes())
				plugin.info("'" + RECIPE_PROPERTY_SUFFIX_OUTPUT + "' not recognized: " + recipeOutput);
			return null;
		}
		int amount = config.getConfig().getInt(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_AMOUNT, 1);

		// output amount
		if (amount < 0) {
			if (plugin.config().logRecipes())
				plugin.info("'" + RECIPE_PROPERTY_SUFFIX_AMOUNT + "' only positive values are allowed: " + amount);
			return null;
		}

		// OP ONLY
		boolean opOnly = plugin.getConfig().getBoolean(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_OP_ONLY, false);

		// each recipe need the rows defined
		// each row represents a 3 column row of the crafting inventory
		// - DCC
		// - CSC
		// - CCC
		List<String> recipeRows = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_ROWS);
		if (recipeRows == null || recipeRows.isEmpty()) {
			if (plugin.config().logRecipes())
				plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_ROWS + "' provided");
			return null;
		}

		// Build a map of Characters to Materials so we can validate that the
		// user has supplied correct materials and so we can build the correct
		// recipe to register with the server
		Map<Character, Material> materialMap = getMaterialMap(recipeKeyRoot);

		if (materialMap == null || materialMap.isEmpty())
			return null;

		// This will be used to cache this exact pattern of Materials
		// We will not register this pattern more than once, but we will store
		// the the
		// first one like it as well as all variants in the
		// materialMatrixHashToRecipesListMap
		// The materialMatrixHashToRecipesListMap will. be used to supply the
		// BlockTyperCraftingListener
		// With all configured recipes. The BlockTyperCraftingListener will
		// determine when the items in the
		// crafting inventory match at least one of the configured recipes for
		// that Material shape
		// Which ever configured recipe matches first will win, so the order
		// that they appear in the recipes list
		// is important if you want to overload recipe material shapes

		if (plugin.config().logRecipes()) {
			plugin.debugInfo("loading materialMatrix");
			plugin.info("parsing pattern: ");
		}

		List<Material> materialMatrix = new ArrayList<Material>();
		int rowNumber = 0;
		for (String row : recipeRows) {
			if (plugin.config().logRecipes())
				plugin.info("  -" + row);
			for (int i = 0; i < 3; i++) {
				Material mat = row.length() > i
						? (materialMap.get(row.charAt(i)) == null ? Material.AIR : materialMap.get(row.charAt(i)))
						: Material.AIR;
				materialMatrix.add((rowNumber * 3) + i, mat);
			}
			rowNumber++;
		}

		// this is not required. It can be used in
		// BlockTyperRecipeCraftListener's method
		// listening for the CraftItemEvent. It will allow users to keep items
		// in their
		// crafting inventories
		// - NNN
		// - NYN
		// - NNN
		List<String> recipeKeepMatrix = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_KEEP);

		if (recipeKeepMatrix != null && plugin.config().logRecipes()) {
			plugin.info("Keep Rows: ");
			for (String keepRow : recipeKeepMatrix) {
				plugin.info("  -" + keepRow);
			}
		}

		List<String> listenersList = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_LISTENERS);

		// Once data is loaded create the recipe and register it
		BlockTyperRecipe recipe = new BlockTyperRecipe(recipeKey, materialMatrix, outputMaterial, plugin);
		recipe.setAmount(amount);
		recipe.setOpOnly(opOnly);
		recipe.setKeepsMatrix(recipeKeepMatrix);
		recipe.setListeners(listenersList);

		String recipeName = config.getConfig().getString(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_NAME);

		// Name of the recipe (will be used for the Display name of the
		// ItemMeta)
		if (recipeName == null || recipeName.trim().isEmpty()) {
			if (plugin.config().logRecipes())
				plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_NAME + "' provided");
			return null;
		} else {
			recipe.setName(recipeName);
			if (plugin.config().logRecipes())
				plugin.info("name: " + recipeName);
		}

		// this is not required. It can be used in
		// BlockTyperRecipeCraftListener's method
		// listening for the PrepareItemCraftEvent. It will require that the
		// crafting inventory
		// contains items in specific locations which has the given hidden
		// recipe key
		// for the item to be successfully crafted
		// -0=recipe-other-item
		// -4=recipe-another-item
		Map<Integer, String> itemHasNbtKeyMatrix = getItemHasNbtKeyMatrixFromConfig(recipeKeyRoot);
		recipe.setItemHasNbtKeyMatrix(itemHasNbtKeyMatrix);

		// Locales (for alternate names and lore)
		List<String> locales = config.getConfig().getStringList(recipeKeyRoot + "." + RECIPE_PROPERTY_SUFFIX_LOCALES);
		recipe.setLocales(locales);
		if (locales != null && !locales.isEmpty()) {
			plugin.debugInfo("Loading locales for recipe: ");
			for (String locale : locales) {
				if (locale == null)
					continue;
				locale = locale.toLowerCase();
				plugin.debugInfo(" -" + locale);
				String key = recipeKeyRoot + "." + locale + RECIPE_PROPERTY_SUFFIX_NAME;
				String localName = config.getConfig().getString(key);
				plugin.debugInfo("Storing name for locale: " + locale + ". Key=" + key + " - Value="
						+ (localName != null ? localName : "") + "");
				recipe.getLocaleNameMap().put(locale, localName);

				key = recipeKeyRoot + "." + locale + RECIPE_PROPERTY_SUFFIX_LORE;
				recipe.getLocaleLoreMap().put(locale, config.getConfig().getStringList(key));
			}
		}

		List<String> lore = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_LORE);
		recipe.setLore(lore);

		return recipe;
	}

	protected final Map<Integer, String> getItemStartsWithMatrixFromConfig(String recipeKeyRoot) {
		List<String> itemStartsWithMatrixConfig = config.getConfig()
				.getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_ITEM_STARTS_WITH);

		Map<Integer, String> itemStartsWithMatrix = getPositionMatrix(itemStartsWithMatrixConfig);

		return itemStartsWithMatrix;
	}

	protected final Map<Integer, String> getItemHasNbtKeyMatrixFromConfig(String recipeKeyRoot) {
		List<String> itemStartsWithMatrixConfig = config.getConfig()
				.getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_ITEM_HAS_NBT_KEY);

		Map<Integer, String> itemHasNbtKeyMatrix = getPositionMatrix(itemStartsWithMatrixConfig);

		return itemHasNbtKeyMatrix;
	}

	protected final Map<Integer, String> getPositionMatrix(List<String> positionEqualsMatrix) {

		Map<Integer, String> positionMatrix = null;

		if (positionEqualsMatrix != null && !positionEqualsMatrix.isEmpty()) {
			positionMatrix = new HashMap<>();

			for (String positionEqualsExpression : positionEqualsMatrix) {
				if (!positionEqualsExpression.contains("=") || positionEqualsExpression.indexOf("=") == 0
						|| ((positionEqualsExpression.indexOf("=") + 1) == positionEqualsExpression.length())) {
					plugin.debugWarning("NOT AN EQUALS EXPRESSION");
					continue;
				}

				String position = positionEqualsExpression.substring(0, positionEqualsExpression.indexOf("="));

				if (position == null || position.isEmpty()) {
					plugin.debugWarning("position == null || position.isEmpty()");
					continue;
				}

				String matchString = positionEqualsExpression.substring(positionEqualsExpression.indexOf("=") + 1);

				positionMatrix.put(Integer.valueOf(position), matchString);
			}
		}

		return positionMatrix;
	}

	protected final Map<Character, Material> getMaterialMap(String recipeKeyRoot) {
		// This is a list of entries in the form key=value
		// The key is a letter from the recipeRows and the value is the Material
		// that it represents
		// -D=DIAMOND
		// -C=COMPASS
		// -S=DIAMOND_SWORD
		List<String> recipeMats = config.getConfig().getStringList(recipeKeyRoot + RECIPE_PROPERTY_SUFFIX_MATS);
		if (recipeMats == null || recipeMats.isEmpty()) {
			if (plugin.config().logRecipes())
				plugin.info("no '" + RECIPE_PROPERTY_SUFFIX_MATS + "' provided");
			return null;
		}

		// Build a map of Characters to Materials so we can validate that the
		// user has supplied correct materials and so we can build the correct
		// recipe to register with the server
		Map<Character, Material> materialMap = new HashMap<Character, Material>();
		if (plugin.config().logRecipes())
			plugin.info("parsing mats: ");
		for (String letterEqualsExpression : recipeMats) {
			if (plugin.config().logRecipes())
				plugin.info("  -" + letterEqualsExpression);

			if (!letterEqualsExpression.contains("=") || letterEqualsExpression.indexOf("=") == 0
					|| ((letterEqualsExpression.indexOf("=") + 1) == letterEqualsExpression.length())) {
				if (plugin.config().logRecipes())
					plugin.warning("not a valid letterEqualsExpression");
				continue;
			}

			String letter = letterEqualsExpression.substring(0, letterEqualsExpression.indexOf("="));

			if (letter == null || letter.isEmpty()) {
				if (plugin.config().logRecipes())
					plugin.warning("letter was null or empty");
			}

			if (materialMap.containsKey(letter.charAt(0))) {
				plugin.debugInfo("materialMap containsKey '" + letter.charAt(0) + "'");
				continue;
			}

			String materialString = letterEqualsExpression.substring(letterEqualsExpression.indexOf("=") + 1);

			if (materialString == null || materialString.isEmpty()) {
				if (plugin.config().logRecipes())
					plugin.warning("materialString was null or empty");
				continue;
			}

			Material material = Material.getMaterial(materialString);

			if (material == null) {
				plugin.debugWarning("material not recognized");
				continue;
			}

			if (plugin.config().logRecipes())
				plugin.debugInfo("materialMap.put(" + letter.charAt(0) + ", " + material + ");");

			if (material == Material.AIR) {
				material = null;
			}

			materialMap.put(letter.charAt(0), material);
		}

		return materialMap;
	}
}
