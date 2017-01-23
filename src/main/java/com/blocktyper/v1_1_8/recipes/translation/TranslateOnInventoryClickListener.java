package com.blocktyper.v1_1_8.recipes.translation;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.blocktyper.v1_1_8.plugin.IBlockTyperPlugin;

public class TranslateOnInventoryClickListener extends ContinuousTranslationListener {

	public TranslateOnInventoryClickListener(IBlockTyperPlugin plugin) {
		super(plugin);
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

}
