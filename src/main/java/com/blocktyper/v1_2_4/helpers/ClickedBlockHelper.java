package com.blocktyper.v1_2_4.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_4.IBlockTyperPlugin;

public class ClickedBlockHelper implements IClickedBlockHelper {

	public static final List<String> DIMENTIONS = Arrays.asList("x", "y", "z");

	private IBlockTyperPlugin plugin;

	public ClickedBlockHelper(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * 
	 */
	public List<String> getMatchesInDimentionItemCount(DimentionItemCount dimentionItemCount, String world, int x,
			int y, int z) {

		if (dimentionItemCount == null)
			return null;

		Map<String, Set<String>> matchesMap = new HashMap<String, Set<String>>();

		String lastDimention = null;
		for (String dimention : DIMENTIONS) {
			if (!dimentionItemCount.getItemsInDimentionAtValue().containsKey(world)) {
				continue;
			}

			Map<String, Map<Integer, Set<String>>> mapForWorld = dimentionItemCount.getItemsInDimentionAtValue()
					.get(world);

			if (mapForWorld == null || !mapForWorld.containsKey(dimention) || mapForWorld.get(dimention) == null
					|| mapForWorld.get(dimention).isEmpty()) {
				plugin.debugInfo("no " + dimention + " values recorded");
				return null;
			}

			int coordValue = dimention.equals("x") ? x : (dimention.equals("y") ? y : z);

			if (!dimentionItemCount.getItemsInDimentionAtValue().get(world).get(dimention).containsKey(coordValue)
					|| dimentionItemCount.getItemsInDimentionAtValue().get(world).get(dimention).get(coordValue)
							.isEmpty()) {
				plugin.debugInfo("no matching " + dimention + " value");
				return null;
			} else {

				Set<String> newMatchesList = new HashSet<String>();
				if (lastDimention == null || matchesMap.containsKey(lastDimention)) {
					for (String uuid : mapForWorld.get(dimention).get(coordValue)) {
						if (lastDimention == null || matchesMap.get(lastDimention).contains(uuid)) {
							newMatchesList.add(uuid);
						}
					}
				}

				matchesMap.put(dimention, newMatchesList);
			}
			lastDimention = dimention;
		}

		List<String> exactMatches = null;
		if (lastDimention != null && matchesMap.containsKey(lastDimention)) {
			exactMatches = new ArrayList<String>(matchesMap.get(lastDimention));
		}

		return exactMatches;
	}

	/**
	 * 
	 */
	public DimentionItemCount removeIdFromDimentionItemCount(String idToRemove, DimentionItemCount dimentionItemCount) {

		for (String world : dimentionItemCount.getItemsInDimentionAtValue().keySet()) {
			Map<String, Map<Integer, Set<String>>> mapForWorld = dimentionItemCount.getItemsInDimentionAtValue()
					.get(world);

			for (String dimention : ClickedBlockHelper.DIMENTIONS) {
				if (mapForWorld == null || !mapForWorld.containsKey(dimention) || mapForWorld.get(dimention) == null
						|| mapForWorld.get(dimention).isEmpty()) {
					continue;
				}
				Map<Integer, Set<String>> mapForDimention = mapForWorld.get(dimention);

				List<Integer> coordsToRemove = new ArrayList<Integer>();
				for (Integer coord : mapForDimention.keySet()) {
					Set<String> set = mapForDimention.get(coord);

					if (set == null || set.isEmpty() || !set.contains(idToRemove))
						continue;

					set.remove(idToRemove);

					if (set == null || set.isEmpty()) {
						coordsToRemove.add(coord);
					} else {
						mapForDimention.put(coord, set);
					}
				}

				if (!coordsToRemove.isEmpty()) {
					for (Integer coordToRemove : coordsToRemove) {
						mapForDimention.remove(coordToRemove);
					}
				}

				mapForWorld.put(dimention, mapForDimention);

			}
		}

		return dimentionItemCount;
	}

	/**
	 * 
	 */
	public PlacementOrientation getPlacementOrientation(Location playerLocation, Location clickedLocation) {

		int playerX = playerLocation.getBlockX();
		int playerZ = playerLocation.getBlockZ();

		int blockX = clickedLocation.getBlockX();
		int blockZ = clickedLocation.getBlockZ();

		int dx = playerX - blockX;
		int dz = playerZ - blockZ;

		if ((dx == 0 && dz == 0) || (dx != 0 && dz != 0)) {
			return null;
		}

		PlacementOrientation placementOrientation = new PlacementOrientation();
		if (dz != 0) {
			placementOrientation.setOrientation(PlacementOrientation.X);
			placementOrientation.setPositive(dz > 0);
			placementOrientation.setAway(dz < 0);
		} else {
			placementOrientation.setOrientation(PlacementOrientation.Z);
			placementOrientation.setPositive(dx < 0);
			placementOrientation.setAway(dx < 0);
		}

		return placementOrientation;
	}

	/**
	 * 
	 */

	public boolean itemMatchesComplexMaterial(ItemStack item, ComplexMaterial complexMaterial,
			boolean allowDisplayName) {
		if(item == null){
			return false;
		}
		if (allowDisplayName || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
			return new ComplexMaterial(item).equals(complexMaterial);
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	public boolean blockMatchesComplexMaterial(Block block, ComplexMaterial complexMaterial) {
		if(block == null){
			return false;
		}
		return new ComplexMaterial(block).equals(complexMaterial);
	}

}
