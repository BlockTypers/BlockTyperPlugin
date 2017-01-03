package com.blocktyper.serialization;

import java.io.Serializable;

import org.bukkit.enchantments.Enchantment;

public class CardboardEnchantment  implements Serializable {
    private static final long serialVersionUID = 201611061552L;
 
    private final String name;
 
	public CardboardEnchantment(Enchantment enchantment) {
        this.name = enchantment.getName();
    }
 
	public Enchantment unbox() {
        return Enchantment.getByName(name);
    }
}
