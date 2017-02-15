package com.blocktyper.v1_2_2.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.v1_2_2.IBlockTyperPlugin;
import com.blocktyper.v1_2_2.config.BlockTyperConfig;
import com.blocktyper.v1_2_2.nbt.NBTItem;

public class RecipeCraftingListener implements Listener {

	protected IBlockTyperPlugin plugin;
	protected BlockTyperConfig config;
	protected IBlockTyperRecipeRegistrar recipeRegistrar;

	public RecipeCraftingListener(IBlockTyperPlugin plugin, IBlockTyperRecipeRegistrar recipeRegistrar) {
		this.plugin = plugin;
		this.config = plugin.config();
		this.recipeRegistrar = recipeRegistrar;
	}

	public List<IRecipe> getRecipesFromMaterialMatrixHash(int materialMatrixHash) {
		return recipeRegistrar != null ? recipeRegistrar.getRecipesFromMaterialMatrixHash(materialMatrixHash) : null;
	}

	private static class MaterialMatrixHash {
		Integer hash;
		Map<Integer, ItemStack> positionMap;
	}

	@SuppressWarnings("deprecation")
	private MaterialMatrixHash getMaterialMatrixHash(ItemStack[] craftingMatrix) {

		plugin.debugInfo("craftingMatrix length: " + (craftingMatrix == null ? 0 : craftingMatrix.length));

		List<Material> materialMatrix = new ArrayList<Material>();
		List<Byte> materialDataMatrix = new ArrayList<Byte>();

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

			Byte data = item.getData() != null ? item.getData().getData() : 0;
			plugin.debugInfo("###########  MAT: " + item.getType());
			plugin.debugInfo("########### DATA: " + data);
			materialDataMatrix.add(data);

			positionInt++;
		}

		MaterialMatrixHash materialMatrixHash = new MaterialMatrixHash();
		materialMatrixHash.hash = AbstractBlockTyperRecipe.initMaterialMatrixHash(materialMatrix, materialDataMatrix);
		materialMatrixHash.positionMap = positionMap;

		return materialMatrixHash;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onCraftItem(CraftItemEvent event) {
		plugin.debugInfo("CraftItemEvent event");

		MaterialMatrixHash materialMatrixHash = getMaterialMatrixHash(event.getInventory().getMatrix());
		List<IRecipe> matchingRecipes = getRecipesFromMaterialMatrixHash(materialMatrixHash.hash);

		if (matchingRecipes == null || matchingRecipes.isEmpty()) {
			plugin.debugInfo("NO MATCHING RECIPES");
			return;
		}

		IRecipe exactMatch = getFirstMatch(materialMatrixHash.positionMap, matchingRecipes, event.getWhoClicked());

		if (exactMatch == null) {
			plugin.debugInfo("NO MATCH");
			event.setCancelled(true);
			return;
		}

		plugin.debugInfo("MATCH: " + exactMatch.getName());

		int rowNumber = 0;
		if (exactMatch.getKeepsMatrix() != null && !exactMatch.getKeepsMatrix().isEmpty()) {
			plugin.debugInfo("CHECKING KEEP MATRIX");
			for (String row : exactMatch.getKeepsMatrix()) {

				if (row == null || row.isEmpty()) {
					plugin.debugWarning("keep row was null or empty");
					continue;
				} else {
					plugin.debugInfo("keep row: " + row);
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

							ItemStack copyStack = itemToKeep.clone();

							copyStack.setAmount(1);
							copyStack = (new NBTItem(copyStack)).getItem();

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

		plugin.onCraftItem(event);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {

		plugin.debugInfo("prepareItemCraftEvent event");

		if (event.getInventory() == null || event.getInventory().getMatrix() == null
				|| event.getInventory().getMatrix().length < 1) {

			plugin.debugWarning(
					"event.getInventory() == null || event.getInventory().getMatrix() == null || event.getInventory().getMatrix().length < 1");

			return;
		}

		ItemStack[] craftingMatrix = event.getInventory().getMatrix();

		plugin.debugInfo("craftingMatrix length: " + (craftingMatrix == null ? 0 : craftingMatrix.length));

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
			plugin.debugInfo("materialMatrix.add(" + (item != null ? item.getType() : "null") + ")");

			if (!containsOtherRecipeAsIngredient && item.getItemMeta() != null
					&& item.getItemMeta().getDisplayName() != null) {
				containsOtherRecipeAsIngredient = otherRecipeNames.stream().filter(
						r -> r != null && item.getItemMeta().getDisplayName().toLowerCase().startsWith(r.toLowerCase()))
						.count() > 0;
			}

		}

		MaterialMatrixHash materialMatrixHash = getMaterialMatrixHash(craftingMatrix);

		int hash = materialMatrixHash.hash;

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

		IRecipe recipe = getFirstMatch(materialMatrixHash.positionMap, matchingRecipes,
				(event.getInventory().getViewers() != null && !event.getInventory().getViewers().isEmpty())
						? event.getInventory().getViewers().get(0) : null);

		if (recipe == null) {
			plugin.debugWarning("PrepareItemCraftEvent NO MATCH!");
			event.getInventory().setResult(null);
			return;
		} else {
			plugin.debugInfo("MATCH: " + (recipe.getName() != null ? recipe.getName() : ""));
		}

		HumanEntity player = event.getViewers() != null && !event.getViewers().isEmpty() ? event.getViewers().get(0)
				: null;
		ItemStack result = plugin.recipeRegistrar().getItemFromRecipe(recipe, player, event.getInventory().getResult(),
				null);

		transferSourceLore(result, recipe, materialMatrixHash.positionMap);
		transferSourceEnchantments(result, recipe, materialMatrixHash.positionMap);
		transferSourceName(result, recipe, materialMatrixHash.positionMap);

		event.getInventory().setResult(result);

		plugin.onPrepareItemCraft(event);
	}

	private IRecipe getFirstMatch(Map<Integer, ItemStack> positionMap, List<IRecipe> matchingRecipes,
			HumanEntity player) {

		IRecipe exactMatch = null;

		if (player != null) {
			List<String> enabledWorlds = plugin.getConfig().getStringList(RecipeRegistrar.RECIPES_WORLDS_KEY);
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

			if (recipe.getItemHasNameTagKeyMatrix() != null && !recipe.getItemHasNameTagKeyMatrix().isEmpty()) {
				if (!recipeMatchesTheNametagKeyMatrix(recipe, positionMap)) {
					plugin.debugWarning("getFirstMatch missing required nameTag match.");
					continue;
				}
			}

			if (recipe.getItemHasNbtKeyMatrix() != null && !recipe.getItemHasNbtKeyMatrix().isEmpty()) {
				if (recipeMatchesTheNbtKeyMatrix(recipe, positionMap)) {
					exactMatch = recipe;
				}
			} else {
				exactMatch = recipe;
				break;
			}
		}

		return exactMatch;
	}

	private boolean recipeMatchesTheNbtKeyMatrix(IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		boolean allItemsMatch = true;

		for (Integer position : recipe.getItemHasNbtKeyMatrix().keySet()) {

			if (!allItemsMatch)
				break;

			String nbtKey = recipe.getItemHasNbtKeyMatrix().get(position);

			if (nbtKey == null || nbtKey.isEmpty()) {
				plugin.debugWarning("nbtKey == null || nbtKey.isEmpty()");
				continue;
			}

			if (!positionMap.containsKey(position) || positionMap.get(position) == null) {
				plugin.debugWarning("positionMap does not contain position " + position + ")");
				allItemsMatch = false;
				break;
			}

			String recipesNbtKey = plugin.getRecipesNbtKey();

			NBTItem nbtItem = new NBTItem(positionMap.get(position));
			if (nbtItem == null || !nbtItem.hasKey(recipesNbtKey)) {
				plugin.debugWarning("nbtItem == null || !nbtItem.hasKey(nbtKey)");
				allItemsMatch = false;
				break;
			} else if (!nbtItem.getString(recipesNbtKey).equals(nbtKey)) {
				String keyFound = nbtItem.getString(recipesNbtKey);
				if (!nbtKey.equals(keyFound)) {
					plugin.debugWarning("nbt recipe key did not match. Expected: " + nbtKey + ". Found: " + keyFound);
					allItemsMatch = false;
				}
			}
		}

		return allItemsMatch;
	}

	private boolean recipeMatchesTheNametagKeyMatrix(IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		boolean allItemsMatch = true;

		for (Integer position : recipe.getItemHasNameTagKeyMatrix().keySet()) {

			if (!allItemsMatch)
				break;

			String nameTagText = recipe.getItemHasNameTagKeyMatrix().get(position);

			if (nameTagText == null || nameTagText.isEmpty()) {
				plugin.debugWarning("nameTagText == null || nameTagText.isEmpty()");
				continue;
			}

			if (!positionMap.containsKey(position) || positionMap.get(position) == null) {
				plugin.debugWarning("positionMap does not contain position " + position);
				allItemsMatch = false;
				break;
			}

			if (positionMap.get(position).getType() != Material.NAME_TAG) {
				plugin.debugWarning("positionMap does not contain a nametag at position " + position);
				allItemsMatch = false;
				break;
			}

			if (positionMap.get(position).getItemMeta() == null
					|| !nameTagText.equals(positionMap.get(position).getItemMeta().getDisplayName())) {
				plugin.debugWarning("positionMap does not contain a nametag with the right name at position " + position
						+ ". Expected: " + nameTagText);
				allItemsMatch = false;
				break;
			}

		}

		return allItemsMatch;
	}

	private void transferSourceEnchantments(ItemStack item, IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		if (item == null || recipe == null || positionMap == null)
			return;

		if (recipe.getTransferSourceEnchantmentMatrix() == null
				|| recipe.getTransferSourceEnchantmentMatrix().isEmpty())
			return;

		for (Integer slot : recipe.getTransferSourceEnchantmentMatrix()) {
			if (slot == null)
				continue;
			if (!positionMap.containsKey(slot))
				continue;

			transferSourceEnchantments(item, positionMap.get(slot));
		}
	}

	private void transferSourceName(ItemStack item, IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		if (item == null || recipe == null || positionMap == null)
			return;

		if (recipe.getTransferSourceNameSlot() == null)
			return;

		if (!positionMap.containsKey(recipe.getTransferSourceNameSlot()))
			return;

		transferSourceName(item, positionMap.get(recipe.getTransferSourceNameSlot()));
	}

	private void transferSourceLore(ItemStack item, IRecipe recipe, Map<Integer, ItemStack> positionMap) {
		if (item == null || recipe == null || positionMap == null)
			return;

		if (recipe.getTransferSourceLoreMatrix() == null || recipe.getTransferSourceLoreMatrix().isEmpty())
			return;

		for (Integer slot : recipe.getTransferSourceLoreMatrix()) {
			if (slot == null)
				continue;
			if (!positionMap.containsKey(slot))
				continue;

			transferSourceLore(item, positionMap.get(slot));
		}
	}

	private void transferSourceLore(ItemStack item, ItemStack sourceItem) {
		if (item == null || sourceItem == null || sourceItem.getItemMeta() == null
				|| sourceItem.getItemMeta().getLore() == null)
			return;

		ItemMeta itemMeta = getMetaSafe(item);
		List<String> lore = itemMeta.getLore();

		List<String> newLore = sourceItem.getItemMeta().getLore().stream()
				.filter(l -> !AbstractBlockTyperRecipe.isHiddenRecipeKey(l)).collect(Collectors.toList());

		if (lore == null)
			lore = new ArrayList<>();

		lore.addAll(newLore);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}

	private void transferSourceEnchantments(ItemStack item, ItemStack sourceItem) {
		if (item == null || sourceItem == null || sourceItem.getEnchantments() == null
				|| sourceItem.getEnchantments().entrySet() == null)
			return;

		sourceItem.getEnchantments().entrySet().forEach(e -> transferUnsafeEnchantment(item, e.getKey(), e.getValue()));
	}

	private void transferUnsafeEnchantment(ItemStack itemStack, Enchantment enchantment, int level) {
		if (itemStack == null || enchantment == null)
			return;

		if (itemStack.getEnchantments() != null && itemStack.getEnchantments().containsKey(enchantment)) {
			int existingLevel = itemStack.getEnchantments().get(enchantment);
			if (existingLevel < level) {
				itemStack.addUnsafeEnchantment(enchantment, level);
			}
		} else {
			itemStack.addUnsafeEnchantment(enchantment, level);
		}
	}

	private void transferSourceName(ItemStack item, ItemStack sourceItem) {
		if (item == null || sourceItem == null || sourceItem.getItemMeta() == null
				|| sourceItem.getItemMeta().getDisplayName() == null)
			return;

		ItemMeta itemMeta = getMetaSafe(item);
		itemMeta.setDisplayName(sourceItem.getItemMeta().getDisplayName());
		item.setItemMeta(itemMeta);
	}

	private ItemMeta getMetaSafe(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null ? itemMeta : (new ItemStack(itemStack.getType())).getItemMeta();
	}

}
