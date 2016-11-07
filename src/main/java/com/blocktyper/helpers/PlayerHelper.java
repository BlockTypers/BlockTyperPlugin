package com.blocktyper.helpers;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class PlayerHelper implements IPlayerHelper {

	private IBlockTyperPlugin plugin;

	public PlayerHelper(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemInHand(Player player) {
		if (player == null)
			return null;

		ItemStack itemInHand = player.getItemInHand() != null ? player.getItemInHand()
				: (player.getEquipment() != null && player.getEquipment().getItemInHand() != null
						? player.getEquipment().getItemInHand()
						: (player.getInventory() != null && player.getInventory().getItemInHand() != null
								? player.getInventory().getItemInHand() : null));

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

	public Entity getTargetEntity(Player player) {

		BlockIterator iterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(),
				player.getEyeLocation().getDirection(), 0, 100);
		Entity target = null;
		while (iterator.hasNext()) {
			Block item = iterator.next();
			for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
				int acc = 2;
				for (int x = -acc; x < acc; x++)
					for (int z = -acc; z < acc; z++)
						for (int y = -acc; y < acc; y++)
							if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item)) {
								return target = entity;
							}
			}
		}
		return target;
	}

	public boolean playerCanDoAction(Player player, List<String> permissions) {
		if (player.isOp() || permissions == null || permissions.isEmpty()) {
			return true;
		}

		for (String permission : permissions) {
			if (player.hasPermission(permission)) {
				return true;
			}
		}

		return false;
	}
}
