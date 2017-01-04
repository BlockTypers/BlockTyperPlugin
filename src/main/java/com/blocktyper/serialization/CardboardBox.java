package com.blocktyper.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class CardboardBox implements Serializable {
	private static final long serialVersionUID = 201701032218L;
	 
    private final String type;
    private final int amount;
    private final short damage;
    private final String displayName;
    private final List<String> lore;
 
    private final HashMap<String, Integer> enchants;
 

	public CardboardBox(ItemStack item) {
        this.type = item.getType().name();
        this.amount = item.getAmount();
        this.damage = item.getDurability();
        this.displayName = item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : null;
        
        
        List<String> lore = null;
        if(item.getItemMeta() != null && item.getItemMeta().getLore() != null){
        	lore = item.getItemMeta().getLore();
        }else{
        	lore = new ArrayList<String>();
        }
        this.lore = lore;
        
        HashMap<String, Integer> map = new HashMap<String, Integer>();
 
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
 
        for(Enchantment enchantment : enchantments.keySet()) {
            map.put(new CardboardEnchantment(enchantment).getName(), enchantments.get(enchantment));
        }
 
        this.enchants = map;
    }
 
    public ItemStack unbox() {
		ItemStack item = new ItemStack(Material.getMaterial(type));
		item.setAmount(amount);
		item.setDurability(damage);
 
        HashMap<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();
 
        for(String cEnchantment : enchants.keySet()) {
            map.put(CardboardEnchantment.fromName(cEnchantment).unbox(), enchants.get(cEnchantment));
        }
 
        item.addUnsafeEnchantments(map);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
