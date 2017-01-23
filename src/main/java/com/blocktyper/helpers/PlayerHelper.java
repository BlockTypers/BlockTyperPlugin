package com.blocktyper.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
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

	public boolean itemHasEnchantment(ItemStack item, Enchantment enchantment) {
		if (item != null) {
			if (enchantmentsInclude(item.getEnchantments(), enchantment))
				return true;
			if (item.getItemMeta() != null && enchantmentsInclude(item.getItemMeta().getEnchants(), enchantment))
				return true;
		}
		return false;
	}

	private boolean enchantmentsInclude(Map<Enchantment, Integer> enchantments, Enchantment enchantment) {
		boolean infiniteEnchantExists = false;
		if (enchantments != null && enchantments.keySet() != null && !enchantments.keySet().isEmpty()) {
			infiniteEnchantExists = enchantments.keySet().contains(enchantment);
		}
		return infiniteEnchantExists;
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

	public boolean playerCanDoAction(HumanEntity player, List<String> permissions) {
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

	public String getLanguage(HumanEntity player) {
		String playersLocaleCode = getLocale(player);
		return getLanguageFromLocaleCode(playersLocaleCode);
	}
	
	public String getLanguageFromLocaleCode(String localeCode) {
		if (localeCode != null && localeCode.contains("_"))
			localeCode = localeCode.substring(0, localeCode.indexOf("_"));
		return localeCode;
	}

	public String getLocale(HumanEntity player) {
		if (player == null)
			return plugin.config().getLocale();

		Object ep;
		Field f;
		String locale = null;
		try {
			ep = getMethod("getHandle", player.getClass()).invoke(player, (Object[]) null);
			f = ep.getClass().getDeclaredField("locale");
			f.setAccessible(true);
			locale = (String) f.get(ep);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			plugin.debugWarning("Error getting client locale 1. " + e.getMessage());
		} catch (NoSuchFieldException | SecurityException e) {
			plugin.debugWarning("Error getting client locale 2. " + e.getMessage());
		}
		locale = locale != null ? locale : plugin.config().getLocale();
		locale = locale != null ? locale.toLowerCase() : null;

		plugin.debugInfo("Player locale: " + locale);

		return locale;
	}

	private Method getMethod(String name, Class<?> clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}
}
