package com.blocktyper.recipes;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.nbt.NBTItem;
import com.blocktyper.plugin.IBlockTyperPlugin;

public class ContinuousTranslationListener implements Listener {

	private IBlockTyperPlugin plugin;

	public ContinuousTranslationListener(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/*
	 * ON INVENTORY CLICK
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
			return;
		}
		
		plugin.debugInfo("Attempting inventory click translation");

		Player player = ((Player) event.getWhoClicked());

		event.setCurrentItem(convertItemStackLanguage(event.getCurrentItem(), player));
	}

	/*
	 * ON PLAYER PICK UP
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		Item item = event.getItem();

		if (item == null)
			return;
		
		plugin.debugInfo("Attempting item pickup translation");

		item.setItemStack(convertItemStackLanguage(item.getItemStack(), event.getPlayer()));
	}

	/*
	 * ON INVENTORY OPEN
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void inventoryOpenEvent(InventoryOpenEvent event) {

		if (event.getInventory() == null || event.getInventory().getContents() == null) {
			return;
		}

		HumanEntity player = event.getPlayer();

		if (player == null && event.getInventory().getViewers() != null
				&& !event.getInventory().getViewers().isEmpty()) {
			player = event.getInventory().getViewers().get(0);
		}

		if (player == null) {
			return;
		}
		
		plugin.debugInfo("Attempting inventory open translation");

		for (ItemStack item : event.getInventory().getContents()) {
			if (item != null) {
				convertItemStackLanguage(item, player);
			}
		}
	}

	private ItemStack convertItemStackLanguage(ItemStack itemStack, HumanEntity player) {

		String recipeKey = new NBTItem(itemStack).getString(plugin.getRecipesNbtKey());

		if (recipeKey == null)
			return itemStack;

		IRecipe recipe = plugin.recipeRegistrar().getRecipeFromKey(recipeKey);

		if (recipe == null)
			return itemStack;
		
		plugin.debugInfo("Translating: " + itemStack.getType().name());

		return plugin.recipeRegistrar().getItemFromRecipe(recipe, player, itemStack, itemStack.getAmount(), false);
	}
}
