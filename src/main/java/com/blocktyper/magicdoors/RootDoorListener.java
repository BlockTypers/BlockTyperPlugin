package com.blocktyper.magicdoors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.plugin.BlockTyperPlugin;
import com.blocktyper.recipes.IRecipe;

import net.md_5.bungee.api.ChatColor;

public class RootDoorListener implements Listener {

	public static String RECIPE_NAME_ROOT_DOOR = "magic.doors.recipe.name.root.door";
	
	public static final String DATA_KEY_ROOT_DOORS_IN_DIMENTIONS = "root-doors-dimention";

	private final BlockTyperPlugin plugin;

	private IRecipe magicDoorRecipe;

	public RootDoorListener() {
		plugin = BlockTyperPlugin.plugin;
		String rootDoorName = plugin.config().getConfig().getString(RECIPE_NAME_ROOT_DOOR);
		plugin.info("loading recipe for root-door: '" + rootDoorName + "'");
		magicDoorRecipe = plugin.recipeRegistrar().getRecipeFromKey(rootDoorName);
		
		if(magicDoorRecipe == null){
			plugin.warning("recipe '" + rootDoorName + "' was not found");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityShootBow(EntityShootBowEvent event) {
		plugin.debugInfo("EntityShootBowEvent");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onBlockPlace(BlockPlaceEvent event) {
		plugin.debugInfo("BlockPlaceEvent - Material " + event.getBlock().getType().name());

		if (magicDoorRecipe == null) {
			plugin.debugWarning("No magic door recipe.");
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			plugin.debugWarning("Not holding an item");
			return;
		}
		
		if (!itemInHand.getType().equals(magicDoorRecipe.getOutput())) {
			plugin.debugWarning("Not holding an item which is the same type as the magic door type: " + magicDoorRecipe.getOutput());
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugWarning("Not holding door with a name." );
			return;
		}

		String itemName = itemInHand.getItemMeta().getDisplayName();

		if (!itemName.equals(magicDoorRecipe.getName())) {
			plugin.debugWarning("Not holding door with the magic door name: '" + itemName + "' != '" + magicDoorRecipe.getName() + "'");
			return;
		}
		
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		
		event.getPlayer().sendMessage(ChatColor.GREEN + " you placed a magic door. ID: " + randomUUIDString);
		
		
		DimentionItemCount itemCountPerDimention = plugin.getTypeData(DATA_KEY_ROOT_DOORS_IN_DIMENTIONS, DimentionItemCount.class);
		if (itemCountPerDimention == null) {
			itemCountPerDimention = new DimentionItemCount();
			itemCountPerDimention.itemsInDimentionAtValue = new HashMap<String, Map<Integer, Set<String>>>();
		}
		
		
		
		
		
		List<String> dimentions = new ArrayList<String>();
		dimentions.add("x");
		dimentions.add("y");
		dimentions.add("z");
		
		for(String dimention : dimentions){
			if (itemCountPerDimention.itemsInDimentionAtValue.get(dimention) == null) {
				itemCountPerDimention.itemsInDimentionAtValue.put(dimention, new HashMap<Integer, Set<String>>());
			}
			
			int value = dimention.equals("x") ? event.getBlock().getX() : (dimention.equals("y") ? event.getBlock().getY() : event.getBlock().getZ());
			
			if (itemCountPerDimention.itemsInDimentionAtValue.get(dimention).get(value) == null){
				itemCountPerDimention.itemsInDimentionAtValue.get(dimention).put(value, new HashSet<String>());
			}
			itemCountPerDimention.itemsInDimentionAtValue.get(dimention).get(value).add(randomUUIDString);
		}

		plugin.setData(DATA_KEY_ROOT_DOORS_IN_DIMENTIONS, itemCountPerDimention, true);
		
		event.getPlayer().sendMessage(ChatColor.GREEN + " you placed a magic door. ID: " + randomUUIDString);

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {
		plugin.debugInfo("BlockDamageEvent - Material " + event.getBlock().getType().name());

		DimentionItemCount dimentionItemCount = plugin.getTypeData("x-doors", DimentionItemCount.class);

		if (dimentionItemCount == null || dimentionItemCount.itemsInDimentionAtValue == null
				|| dimentionItemCount.itemsInDimentionAtValue.isEmpty()) {
			plugin.debugInfo("no dimention values recorded");
			return;
		}

		if (!dimentionItemCount.itemsInDimentionAtValue.containsKey("x")
				|| dimentionItemCount.itemsInDimentionAtValue.get("x") == null) {
			plugin.debugInfo("no x values recorded");
			return;
		}
	}

	public class DimentionItemCount {
		public Map<String, Map<Integer, Set<String>>> itemsInDimentionAtValue;
	}

}
