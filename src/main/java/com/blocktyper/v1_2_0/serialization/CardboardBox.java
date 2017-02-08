package com.blocktyper.v1_2_0.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.v1_2_0.nbt.NBTItem;


public class CardboardBox implements Serializable {
	private static final long serialVersionUID = 201701032218L;
	 
    private final String type;
    private final int amount;
    private final short damage;
    private final String displayName;
    private final List<String> lore;
 
    private final HashMap<String, Integer> enchants;
    
    private final HashMap<String, String> nbtStringTags;
    private final HashMap<String, Double> nbtDoubleTags;
    private final HashMap<String, Integer> nbtIntegerTags;
    private final HashMap<String, Boolean> nbtBooleanTags;
 

	public CardboardBox(ItemStack item) {
        this.type = item.getType().name();
        this.amount = item.getAmount();
        this.damage = item.getDurability();
        this.displayName = item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : null;
        this.nbtStringTags = new HashMap<>();
        this.nbtDoubleTags = new HashMap<>();
        this.nbtIntegerTags = new HashMap<>();
        this.nbtBooleanTags = new HashMap<>();
        
        
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
        
        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.getKeys() != null && !nbtItem.getKeys().isEmpty()){
        	for(String key : nbtItem.getKeys()){
        		
        		String asString = nbtItem.getString(key);
        		if(asString != null){
        			nbtStringTags.put(key, asString);
        			
        			if(!asString.isEmpty())
        				continue;
        		}
        		
        		Boolean asBoolean = nbtItem.getBoolean(key);
        		if(asBoolean != null){
        			nbtBooleanTags.put(key, asBoolean);
        			
        			if(asBoolean)
        				continue;
        		}
        		
        		Double asDouble = nbtItem.getDouble(key);
        		if(asDouble != null){
        			nbtDoubleTags.put(key, asDouble);
        			continue;
        		}
        		
        		Integer asInteger = nbtItem.getInteger(key);
        		if(asInteger != null){
        			nbtIntegerTags.put(key, asInteger);
        			continue;
        		}
        		
        	}
        }
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
        
        NBTItem nbtItem = new NBTItem(item);
        
        if(nbtStringTags != null && !nbtStringTags.keySet().isEmpty()){
        	for(String key : nbtStringTags.keySet()){
        		nbtItem.setString(key, nbtStringTags.get(key));
        	}
        }
        
        if(nbtDoubleTags != null && !nbtDoubleTags.keySet().isEmpty()){
        	for(String key : nbtDoubleTags.keySet()){
        		nbtItem.setDouble(key, nbtDoubleTags.get(key));
        	}
        }
        
        if(nbtIntegerTags != null && !nbtIntegerTags.keySet().isEmpty()){
        	for(String key : nbtIntegerTags.keySet()){
        		nbtItem.setInteger(key, nbtIntegerTags.get(key));
        	}
        }
        
        if(nbtBooleanTags != null && !nbtBooleanTags.keySet().isEmpty()){
        	for(String key : nbtBooleanTags.keySet()){
        		nbtItem.setBoolean(key, nbtBooleanTags.get(key));
        	}
        }
        
        return nbtItem.getItem();
    }

}
