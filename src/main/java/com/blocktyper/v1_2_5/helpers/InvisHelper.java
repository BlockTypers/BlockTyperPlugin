package com.blocktyper.v1_2_5.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.v1_2_5.IBlockTyperPlugin;
import com.google.gson.Gson;

public class InvisHelper {

	protected static final int LORE_LINE_LENGTH_LIMIT = 500;

	protected IBlockTyperPlugin plugin;

	protected static final Gson JSON_HELPER = new Gson();

	public InvisHelper(IBlockTyperPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	public <T> void setInvisisbleJson(T obj, ItemStack item, String loreKey, String visiblePrefix) {
		if (obj == null)
			return;
		setInvisisbleJson(item, JSON_HELPER.toJson(obj), loreKey, visiblePrefix);
	}

	public void setInvisisbleJson(ItemStack item, String json, String loreKey, String visiblePrefix) {

		if (item == null) {
			return;
		}

		List<String> relatedTextLines = new ArrayList<>();

		int i = 0;
		while (true) {
			i++;
			String prefix = i + loreKey;

			if (i == 1) {
				prefix = UUID.randomUUID().toString() + ":" + prefix;
			}

			String prefixPlusJson = prefix + json;

			boolean isBreak = prefixPlusJson.length() <= LORE_LINE_LENGTH_LIMIT;

			int endIndex = !isBreak ? LORE_LINE_LENGTH_LIMIT : prefixPlusJson.length();

			relatedTextLines.add(prefixPlusJson.substring(0, endIndex));

			if (isBreak)
				break;

			json = prefixPlusJson.substring(endIndex);
		}

		ItemMeta meta = item.getItemMeta();

		List<String> lore = null;

		if (meta.getLore() != null) {
			lore = meta.getLore().stream().filter(l -> !loreLineMatchesKey(l, loreKey)).collect(Collectors.toList());
		}

		if (lore == null)
			lore = new ArrayList<>();

		List<String> relatedLore = relatedTextLines.stream().map(l -> convertToInvisibleString(l))
				.collect(Collectors.toList());

		if (visiblePrefix != null && relatedLore != null && !relatedLore.isEmpty() && relatedLore.get(0) != null) {
			String newFirstLine = visiblePrefix + relatedLore.get(0);
			relatedLore.set(0, newFirstLine);
		}

		lore.addAll(relatedLore);

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public <T> T getObjectFromInvisisibleLore(ItemStack item, String loreKey, Class<T> type) {

		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
			return null;
		}

		List<String> loreLines = item.getItemMeta().getLore().stream().filter(l -> loreLineMatchesKey(l, loreKey))
				.collect(Collectors.toList());

		if (loreLines == null || loreLines.isEmpty()) {
			return null;
		}

		List<String> lowRawTextLines = loreLines.stream().map(p -> convertToVisibleString(p))
				.collect(Collectors.toList());

		if (lowRawTextLines == null || lowRawTextLines.isEmpty()) {
			return null;
		}

		List<String> objectJsonParts = lowRawTextLines.stream()
				.map(p -> p.substring(p.indexOf(loreKey) + loreKey.length())).collect(Collectors.toList());

		if (objectJsonParts == null || objectJsonParts.isEmpty()) {
			return null;
		}

		String pocketJson = objectJsonParts.stream().reduce("", (a, b) -> a + b);

		T obj = plugin.deserializeJsonSafe(pocketJson, type);

		if (obj == null) {
			plugin.warning("There was an unexpected issue deserialing the object.");
			plugin.warning("------");
			plugin.warning("Parts[" + objectJsonParts.size() + "]: ");
			objectJsonParts.forEach(p -> plugin.debugWarning("  -PART: " + p));
			plugin.warning("------");
			plugin.warning("------");
			plugin.warning("LORE ITEMS[" + item.getItemMeta().getLore().size() + "]");
			item.getItemMeta().getLore().forEach(l -> plugin.debugWarning(" -Lore: " + convertToVisibleString(l)));
			plugin.warning("------");
		}

		return obj;
	}

	public static List<String> removeLoreWithInvisibleKey(ItemStack item, String invisibelKey) {
		if (item == null)
			return null;

		List<String> lore = getNonInvisibleLore(item, invisibelKey);
		if (lore == null)
			lore = new ArrayList<>();

		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return lore;
	}

	public static List<String> getNonInvisibleLore(ItemStack item, String loreKey) {
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null
				|| item.getItemMeta().getLore().isEmpty())
			return null;

		return item.getItemMeta().getLore().stream().filter(l -> !loreLineMatchesKey(l, loreKey))
				.collect(Collectors.toList());
	}
	
	public static List<String> getInvisibleLore(ItemStack item, String loreKey) {
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null
				|| item.getItemMeta().getLore().isEmpty())
			return null;

		return item.getItemMeta().getLore().stream().filter(l -> loreLineMatchesKey(l, loreKey))
				.collect(Collectors.toList());
	}

	protected static boolean loreLineMatchesKey(String loreLine, String key) {
		if (loreLine == null || loreLine.isEmpty())
			return false;

		loreLine = convertToVisibleString(loreLine);

		return loreLine.contains(key);
	}

	public static String convertToInvisibleString(String s) {
		String hidden = "";
		for (char c : s.toCharArray())
			hidden += ChatColor.COLOR_CHAR + "" + c;
		return hidden;
	}

	public static String convertToVisibleString(String s) {
		if (s != null && !s.isEmpty()) {
			s = s.replace(ChatColor.COLOR_CHAR + "", "");
		}

		return s;
	}
}
