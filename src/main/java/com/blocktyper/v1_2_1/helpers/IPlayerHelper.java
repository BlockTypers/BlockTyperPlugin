package com.blocktyper.v1_2_1.helpers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public interface IPlayerHelper {
	ItemStack getItemInHand(Player player);

	ItemStack getFirstArrowStack(Player player);

	Entity getTargetEntity(Player player);

	boolean playerCanDoAction(HumanEntity player, List<String> permissions);

	boolean itemHasEnchantment(ItemStack item, Enchantment enchantment);

	String getLocale(HumanEntity player);

	String getLanguage(HumanEntity player);
	
	String getLanguageFromLocaleCode(String localeCode);
	
	int getAmountOfMaterialInBag(HumanEntity player, ComplexMaterial complexMaterial, boolean allowDisplayName);
	
	void spendMaterialsInBag(Map<ComplexMaterial, Integer> costMap, HumanEntity player);
	
	void tryToFitItemInPlayerInventory(ItemStack item, HumanEntity player);
	
	boolean updateCooldownIfPossible(Map<String, Date> coolDownMap, HumanEntity player, double coolDownSeconds);
}
