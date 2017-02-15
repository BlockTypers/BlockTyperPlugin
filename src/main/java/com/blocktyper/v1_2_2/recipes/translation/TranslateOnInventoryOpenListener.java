package com.blocktyper.v1_2_2.recipes.translation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_2.IBlockTyperPlugin;

public class TranslateOnInventoryOpenListener extends ContinuousTranslationListener {

	public TranslateOnInventoryOpenListener(IBlockTyperPlugin plugin) {
		super(plugin);
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

		List<ItemStack> newContents = new ArrayList<>();
		for (ItemStack item : event.getInventory().getContents()) {
			if (item != null) {
				newContents.add(convertItemStackLanguage(item, player));
			} else {
				newContents.add(item);
			}
		}
		event.getInventory().setContents(newContents.toArray(new ItemStack[newContents.size()]));
	}
}
