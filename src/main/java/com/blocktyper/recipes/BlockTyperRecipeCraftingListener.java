package com.blocktyper.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.plugin.IBlockTyperPlugin;

public class BlockTyperRecipeCraftingListener implements Listener {

	protected IBlockTyperPlugin plugin;
	protected BlockTyperConfig config;
	protected IBlockTyperRecipeRegistrar recipeRegistrar;

	public BlockTyperRecipeCraftingListener(IBlockTyperPlugin plugin, IBlockTyperRecipeRegistrar recipeRegistrar) {
		this.plugin = plugin;
		this.config = plugin.config();
		this.recipeRegistrar = recipeRegistrar;
	}

	public List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash) {
		return recipeRegistrar != null ? recipeRegistrar.getRecipesFromMaterialMatrixHash(materialMatrixHash) : null;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void craftItemEvent(CraftItemEvent event) {
		plugin.debugInfo("CraftItemEvent event");

		ItemStack[] craftingMatrix = event.getInventory().getMatrix();

		plugin.debugInfo("craftingMatrix length: " + (craftingMatrix == null ? 0 : craftingMatrix.length));

		List<Material> materialMatrix = new ArrayList<Material>();

		Map<Integer, ItemStack> positionMap = new HashMap<Integer, ItemStack>();
		int positionInt = 0;
		for (ItemStack item : craftingMatrix) {

			if (item == null) {
				plugin.debugInfo("null item");
				continue;
			}
			plugin.debugInfo("position: " + positionInt + " - " + item.getType().name() + " - "
					+ (item.getItemMeta() != null ? (" - " + item.getItemMeta().getDisplayName()) : ""));

			positionMap.put(positionInt, item);
			materialMatrix.add(item != null ? item.getType() : null);
			positionInt++;
		}

		List<IRecipe> matchingRecipes = getRecipesFromMaterialMatrixHash(
				BlockTyperRecipe.initMaterialMatrixHash(materialMatrix));

		if (matchingRecipes == null || matchingRecipes.isEmpty()) {
			if (config.debugEnabled())
				plugin.info("NO MATCHING RECIPES");
			return;
		}

		IRecipe exactMatch = getFirstMatch(positionMap, matchingRecipes, event.getWhoClicked());

		if (exactMatch == null) {
			if (config.debugEnabled())
				plugin.info("NO MATCH");
			event.setCancelled(true);
			return;
		}

		if (config.debugEnabled())
			plugin.info("MATCH: " + exactMatch.getName());

		int rowNumber = 0;
		if (exactMatch.getKeepsMatrix() != null && !exactMatch.getKeepsMatrix().isEmpty()) {
			if (config.debugEnabled())
				plugin.info("CHECKING KEEP MATRIX");

			for (String row : exactMatch.getKeepsMatrix()) {

				if (row == null || row.isEmpty()) {
					if (config.debugEnabled())
						plugin.warning("keep row was null or empty");
					continue;
				} else {
					if (config.debugEnabled())
						plugin.info("keep row: " + row);
				}

				for (int i = 0; i < 3; i++) {
					boolean keep = false;
					int index = (rowNumber * 3) + i + 1;
					if (row.length() >= i + 1) {
						Character keepCharacter = row.charAt(i);
						keep = keepCharacter == 'Y';
					}
					if (keep) {
						ItemStack itemToKeep = event.getInventory().getItem(index);

						if (itemToKeep != null) {
							plugin.debugInfo("KEEPING ITEM(" + index + "): " + itemToKeep.getType().name());

							ItemStack copyStack = new ItemStack(itemToKeep.getType());

							if (itemToKeep.getItemMeta() != null) {
								copyStack.setItemMeta(itemToKeep.getItemMeta());
							}

							if (itemToKeep.getEnchantments() != null) {
								copyStack.addEnchantments(itemToKeep.getEnchantments());
							}

							if (itemToKeep.getData() != null) {
								copyStack.setData(itemToKeep.getData());
							}

							copyStack.setDurability(itemToKeep.getDurability());

							HumanEntity player = (event.getInventory().getViewers() != null
									&& !event.getInventory().getViewers().isEmpty())
											? event.getInventory().getViewers().get(0) : null;

							if (player != null) {
								player.getWorld().dropItem(player.getLocation(), copyStack);
								plugin.debugInfo("Item dropped.");
							}
						} else {
							if (config.debugEnabled())
								plugin.warning("Cannot keep item at index: " + index + ". There is nothing there.");
						}

					} else {
						plugin.debugInfo("Not keeping item at index: " + index);
					}
				}
				rowNumber++;
			}
		} else {
			plugin.debugInfo("no KEEP MATRIX");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void prepareItemCraft(PrepareItemCraftEvent event) {

		plugin.debugInfo("prepareItemCraftEvent event");

		if (event.getInventory() == null || event.getInventory().getMatrix() == null
				|| event.getInventory().getMatrix().length < 1) {

			plugin.debugWarning(
					"event.getInventory() == null || event.getInventory().getMatrix() == null || event.getInventory().getMatrix().length < 1");

			return;
		}

		ItemStack[] craftingMatrix = event.getInventory().getMatrix();

		plugin.debugInfo("craftingMatrix length: " + (craftingMatrix == null ? 0 : craftingMatrix.length));

		List<Material> materialMatrix = new ArrayList<Material>();

		Map<Integer, ItemStack> positionMap = new HashMap<Integer, ItemStack>();
		int positionInt = 0;

		Set<String> otherRecipeNames = new HashSet<String>();
		List<IRecipe> otherRecipes = recipeRegistrar.getRecipes();

		if (otherRecipes != null && !otherRecipes.isEmpty()) {
			for (IRecipe otherRecipe : otherRecipes) {
				if (otherRecipe != null) {
					otherRecipeNames.add(otherRecipe.getName());
					plugin.debugInfo("other recipe: " + otherRecipe.getName());
				} else {
					plugin.debugInfo("other recipe was NULL!");
				}

			}
		}

		boolean containsOtherRecipeAsIngredient = false;

		for (ItemStack item : craftingMatrix) {
			positionMap.put(positionInt, item);
			materialMatrix.add(item != null ? item.getType() : null);

			if (config.debugEnabled())
				plugin.info("materialMatrix.add(" + (item != null ? item.getType() : "null") + ")");

			positionInt++;

			if (!containsOtherRecipeAsIngredient && item.getItemMeta() != null
					&& item.getItemMeta().getDisplayName() != null) {
				containsOtherRecipeAsIngredient = otherRecipeNames.stream().filter(
						r -> r != null && item.getItemMeta().getDisplayName().toLowerCase().startsWith(r.toLowerCase()))
						.count() > 0;
			}

		}

		int hash = BlockTyperRecipe.initMaterialMatrixHash(materialMatrix);

		List<IRecipe> matchingRecipes = getRecipesFromMaterialMatrixHash(hash);

		if (matchingRecipes == null || matchingRecipes.isEmpty()) {
			plugin.debugInfo("No matchingRecipes found for hash: " + hash);

			if (containsOtherRecipeAsIngredient) {
				plugin.debugInfo("matchingRecipes == null && containsOtherRecipeAsIngredient");
				event.getInventory().setResult(null);
			} else {
				plugin.debugInfo("does not contain other recipes as ingredient");
			}

			return;
		}

		plugin.debugInfo("matchingRecipes found for hash: " + hash);

		IRecipe recipe = getFirstMatch(positionMap, matchingRecipes,
				(event.getInventory().getViewers() != null && !event.getInventory().getViewers().isEmpty())
						? event.getInventory().getViewers().get(0) : null);

		if (recipe == null) {
			plugin.debugWarning("PrepareItemCraftEvent NO MATCH!");
			event.getInventory().setResult(null);
			return;
		} else {
			plugin.debugInfo("MATCH: " + (recipe.getName() != null ? recipe.getName() : ""));
		}

		HumanEntity player = event.getViewers() != null && !event.getViewers().isEmpty() ? event.getViewers().get(0) : null;
		ItemStack result = plugin.recipeRegistrar().getItemFromRecipe(recipe, player, event.getInventory().getResult(), null);
		event.getInventory().setResult(result);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void furnaceSmeltMushroom(FurnaceSmeltEvent event) {

		plugin.debugInfo("FurnaceSmeltEvent event");

		ItemStack result = event.getResult();

		ItemStack[] craftingMatrix = new ItemStack[1];
		craftingMatrix[0] = result;

		List<Material> materialMatrix = new ArrayList<Material>();

		Map<Integer, ItemStack> positionMap = new HashMap<Integer, ItemStack>();
		int positionInt = 0;
		for (ItemStack item : craftingMatrix) {
			positionMap.put(positionInt, item);
			materialMatrix.add(item != null ? item.getType() : null);
		}

		List<IRecipe> matchingRecipes = getRecipesFromMaterialMatrixHash(
				BlockTyperRecipe.initMaterialMatrixHash(materialMatrix));

		if (matchingRecipes == null || matchingRecipes.isEmpty())
			return;

		IRecipe exactMatch = getFirstMatch(positionMap, matchingRecipes, null);

		if (exactMatch == null) {
			event.setCancelled(true);
			return;
		} else {
			ItemMeta itemMeta = result.getItemMeta();
			itemMeta.setDisplayName(exactMatch.getName());
			result.setItemMeta(itemMeta);
			event.setResult(result);
		}
	}

	private IRecipe getFirstMatch(Map<Integer, ItemStack> positionMap, List<IRecipe> matchingRecipes,
			HumanEntity player) {

		IRecipe exactMatch = null;

		if (player != null) {
			List<String> enabledWorlds = plugin.getConfig().getStringList(BlockTyperRecipeRegistrar.RECIPES_WORLDS_KEY);
			if (enabledWorlds != null && !enabledWorlds.isEmpty()) {
				if (!enabledWorlds.contains(player.getWorld().getName())) {
					plugin.debugInfo("World not enabled for block typer recipes");
					return null;
				} else {
					plugin.debugInfo("World is enabled for block typer recipes");
				}
			} else {
				plugin.debugInfo("All worlds are enabled for block typer recipes");
			}
		}

		for (IRecipe recipe : matchingRecipes) {

			if (recipe == null) {
				plugin.debugWarning("getFirstMatch recipe == null!");
				continue;
			}

			if (recipe.isOpOnly() && (player == null || !player.isOp())) {
				plugin.debugWarning("getFirstMatch op only recipe.");
				continue;
			}

			if (recipe.getItemHasHiddenKeyMatrix() != null && !recipe.getItemHasHiddenKeyMatrix().isEmpty()) {
				if (recipeMatchesTheHiddenKeyMatrix(recipe, positionMap)) {
					exactMatch = recipe;
				}
			} else if (recipeMatchesTheStartsWithMatrix(recipe, positionMap)) {
				exactMatch = recipe;
			}
		}

		return exactMatch;
	}

	private boolean recipeMatchesTheHiddenKeyMatrix(IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		boolean allItemsMatch = true;

		for (Integer position : recipe.getItemHasHiddenKeyMatrix().keySet()) {

			if (!allItemsMatch)
				break;

			String hiddenKey = recipe.getItemHasHiddenKeyMatrix().get(position);

			if (hiddenKey == null || hiddenKey.isEmpty()) {
				plugin.debugWarning("hiddenKey == null || hiddenKey.isEmpty()");
				continue;
			}

			if (!positionMap.containsKey(position) || positionMap.get(position) == null) {
				plugin.debugWarning("positionMap does not contain position " + position + ")");
				allItemsMatch = false;
				break;
			}

			ItemMeta meta = positionMap.get(position).getItemMeta();

			if (meta == null || meta.getLore() == null) {
				plugin.debugWarning("meta == null || meta.getLore() == null");
				allItemsMatch = false;
				break;
			} else if (!meta.getLore().stream().anyMatch(l -> loreHasHiddenKey(l, hiddenKey))) {
				plugin.debugWarning("lore did not contain hidden recipe key");
				allItemsMatch = false;
				break;
			}

		}

		return allItemsMatch;
	}

	boolean loreHasHiddenKey(String lore, String hiddenKey) {
		return lore != null
				&& plugin.getInvisibleLoreHelper().convertToVisibleString(lore).equals(BlockTyperRecipe.getRecipeKeyToBeHidden(hiddenKey));
	}

	private boolean recipeMatchesTheStartsWithMatrix(IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		boolean allItemsMatch = true;

		if (recipe.getItemStartsWithMatrix() == null || recipe.getItemStartsWithMatrix().isEmpty()) {

			plugin.debugInfo("recipe.getItemStartsWithMatrix() == null || recipe.getItemStartsWithMatrix().isEmpty()");

			boolean itemsNotNamedOrMatchMaterial = true;

			for (ItemStack item : positionMap.values()) {
				if (item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null
						&& !item.getItemMeta().getDisplayName().toLowerCase()
								.startsWith(item.getType().name().toLowerCase().replace("_", " "))) {
					plugin.debugWarning("named item was not named like material [" + item.getItemMeta().getDisplayName()
							+ "][" + item.getType().name() + "].");
					itemsNotNamedOrMatchMaterial = false;
					break;
				}
			}

			if (!itemsNotNamedOrMatchMaterial)
				return false;

			return true;
		} else {
			for (Integer position : recipe.getItemStartsWithMatrix().keySet()) {

				if (!allItemsMatch)
					break;

				String matchString = recipe.getItemStartsWithMatrix().get(position);

				if (matchString == null || matchString.isEmpty()) {
					plugin.debugWarning("matchString == null || matchString.isEmpty()");
					continue;
				}

				if (matchString.startsWith("{{") && matchString.endsWith("}}")) {
					matchString = matchString.substring(2, matchString.lastIndexOf("}}"));

					if (plugin.getConfig().contains(matchString)) {
						plugin.debugWarning("match string found in config {{" + matchString + "}}");
						matchString = plugin.getConfig().getString(matchString);
						plugin.debugWarning("value: " + matchString);
					} else {
						plugin.debugWarning("config does not contain {{" + matchString + "}}");
						allItemsMatch = false;
						break;
					}
				}

				if (!positionMap.containsKey(position) || positionMap.get(position) == null) {
					plugin.debugWarning("positionMap does not contain position " + position + ")");
					allItemsMatch = false;
					break;
				}

				ItemMeta meta = positionMap.get(position).getItemMeta();

				if (meta == null || meta.getDisplayName() == null) {
					plugin.debugWarning("meta == null || meta.getDisplayName() == null");
					allItemsMatch = false;
					break;
				} else if (!meta.getDisplayName().startsWith(matchString)) {
					plugin.debugWarning("!\"" + meta.getDisplayName() + "\".startsWith(matchString)");
					allItemsMatch = false;
					break;
				}

			}
		}

		return allItemsMatch;
	}

}
