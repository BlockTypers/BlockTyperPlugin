package com.blocktyper.helpers;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPlayerHelper {
	ItemStack getItemInHand(Player player);
	ItemStack getFirstArrowStack(Player player);
	Entity getTargetEntity(Player player);
	public boolean playerCanDoAction(Player player, List<String> permissions);
}
