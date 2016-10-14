package com.blocktyper.helpers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerHelper implements IPlayerHelper{
	@SuppressWarnings("deprecation")
	public ItemStack getItemInHand(Player player){
		ItemStack itemInHand = player.getItemInHand() != null ? player.getItemInHand()
				: (player.getEquipment().getItemInHand() != null ? player.getEquipment().getItemInHand()
						: (player.getInventory().getItemInHand() != null ? player.getInventory().getItemInHand()
								: null));
		
		return itemInHand;
	}
}
