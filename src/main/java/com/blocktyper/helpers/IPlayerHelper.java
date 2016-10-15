package com.blocktyper.helpers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPlayerHelper {
	ItemStack getItemInHand(Player player);
	ItemStack getFirstArrowStack(Player player);
}
