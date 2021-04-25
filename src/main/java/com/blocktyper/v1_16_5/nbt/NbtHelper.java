package com.blocktyper.v1_16_5.nbt;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_16_5.recipes.IRecipe;


public class NbtHelper {

	
	public static void replaceUniqueNbtItemInInventory(HumanEntity player, ItemStack item, String uniqueId, Inventory inventory) {

		if (inventory != null && inventory.getContents() != null) {
			ItemWithIndex itemWithIndex = getMathcingItemAndIndexByUniqueId(uniqueId, inventory);
			
			if (itemWithIndex != null) {
				ItemStack[] contents = inventory.getContents();
				contents[itemWithIndex.index] = new NBTItem(item).getItem();
				inventory.setContents(contents);
			} else {
				return;
			}
		}
	}
	
	
	public static ItemStack getMathcingItemByUniqueId(String uniqueId, Inventory inventory) {
		ItemWithIndex itemWithIndex = getMathcingItemAndIndexByUniqueId(uniqueId, inventory);
		return itemWithIndex != null ? itemWithIndex.item : null;
	}
	
	private static ItemWithIndex getMathcingItemAndIndexByUniqueId(String uniqueId, Inventory inventory) {

		if (inventory != null && inventory.getContents() != null) {
			int index = 0;
			for (ItemStack itemInInventory : inventory.getContents()) {
				if (itemInInventory != null) {
					NBTItem nbtItem = new NBTItem(itemInInventory);
					if (uniqueId.equals(nbtItem.getString(IRecipe.NBT_BLOCKTYPER_UNIQUE_ID))) {
						return new ItemWithIndex(itemInInventory, index);
					}
				}
				index++;
			}
		}
		
		return null;
	}
	
	private static class ItemWithIndex{
		private ItemStack item;
		private int index;
		
		public ItemWithIndex(ItemStack item, int index) {
			super();
			this.item = item;
			this.index = index;
		}
	}
}
