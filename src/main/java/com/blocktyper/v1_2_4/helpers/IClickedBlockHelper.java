package com.blocktyper.v1_2_4.helpers;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface IClickedBlockHelper {
	List<String> getMatchesInDimentionItemCount(DimentionItemCount dimentionItemCount, String world, int x, int y,
			int z);

	DimentionItemCount removeIdFromDimentionItemCount(String idToRemove, DimentionItemCount dimentionItemCount);

	boolean itemMatchesComplexMaterial(ItemStack item, ComplexMaterial complexMaterial, boolean allowDisplayName);

	boolean blockMatchesComplexMaterial(Block block, ComplexMaterial complexMaterial);

	PlacementOrientation getPlacementOrientation(Location playerLocation, Location clickedLocation);

	public static class PlacementOrientation {
		public static int X = 1;
		public static int Z = 0;

		private int orientation = -1;
		private boolean positive;
		private boolean away;

		public int getOrientation() {
			return orientation;
		}

		public void setOrientation(int orientation) {
			this.orientation = orientation;
		}

		public boolean isPositive() {
			return positive;
		}

		public void setPositive(boolean positive) {
			this.positive = positive;
		}

		public boolean isAway() {
			return away;
		}

		public void setAway(boolean away) {
			this.away = away;
		}
	}
}
