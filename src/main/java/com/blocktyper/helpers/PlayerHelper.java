package com.blocktyper.helpers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class PlayerHelper implements IPlayerHelper {

	private IBlockTyperPlugin plugin;

	public PlayerHelper(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemInHand(Player player) {
		ItemStack itemInHand = player.getItemInHand() != null ? player.getItemInHand()
				: (player.getEquipment().getItemInHand() != null ? player.getEquipment().getItemInHand()
						: (player.getInventory().getItemInHand() != null ? player.getInventory().getItemInHand()
								: null));

		return itemInHand;
	}

	public ItemStack getFirstArrowStack(Player player) {
		ItemStack firstArrowStack = null;

		if (player.getInventory().getStorageContents() != null) {
			int i = 0;
			for (ItemStack item : player.getInventory().getStorageContents()) {
				i++;
				if (item == null)
					continue;

				Material material = item.getType();
				String log = i + " - " + material.name() + " - ["
						+ (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null
								? item.getItemMeta().getDisplayName() : "")
						+ "]";
				plugin.debugInfo(log);

				if (material.equals(Material.ARROW)) {
					firstArrowStack = item;
					break;
				}
			}

		}

		return firstArrowStack;
	}
}
