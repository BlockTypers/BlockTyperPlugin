package com.blocktyper.recipes.translation;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class TranslateOnPickupListener extends ContinuousTranslationListener {

	public TranslateOnPickupListener(IBlockTyperPlugin plugin) {
		super(plugin);
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
}
