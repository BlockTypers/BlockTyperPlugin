package com.blocktyper.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blocktyper.plugin.IBlockTyperPlugin;

public class ClickedBlockHelper implements IClickedBlockHelper {

	public static final List<String> DIMENTIONS = Arrays.asList("x", "y", "z");
	
	private IBlockTyperPlugin plugin;

	public ClickedBlockHelper(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	public List<String> getMatchesInDimentionItemCount(DimentionItemCount dimentionItemCount, int x, int y, int z) {

		if (dimentionItemCount == null)
			return null;

		Map<String, Set<String>> matchesMap = new HashMap<String, Set<String>>();

		String lastDimention = null;
		for (String dimention : DIMENTIONS) {
			if (!dimentionItemCount.getItemsInDimentionAtValue().containsKey(dimention)
					|| dimentionItemCount.getItemsInDimentionAtValue().get(dimention) == null
					|| dimentionItemCount.getItemsInDimentionAtValue().get(dimention).isEmpty()) {
				plugin.debugInfo("no " + dimention + " values recorded");
				return null;
			}

			int coordValue = dimention.equals("x") ? x : (dimention.equals("y") ? y : z);

			if (!dimentionItemCount.getItemsInDimentionAtValue().get(dimention).containsKey(coordValue)
					|| dimentionItemCount.getItemsInDimentionAtValue().get(dimention).get(coordValue).isEmpty()) {
				plugin.debugInfo("no matching " + dimention + " value");
				return null;
			} else {

				Set<String> newMatchesList = new HashSet<String>();
				if (lastDimention == null || matchesMap.containsKey(lastDimention)) {
					for (String uuid : dimentionItemCount.getItemsInDimentionAtValue().get(dimention).get(coordValue)) {
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
	
	
	public DimentionItemCount removeIdFromDimentionItemCount(String idToRemove, DimentionItemCount dimentionItemCount){
		
		for (String dimention : ClickedBlockHelper.DIMENTIONS) {
			if (!dimentionItemCount.getItemsInDimentionAtValue().containsKey(dimention)
					|| dimentionItemCount.getItemsInDimentionAtValue().get(dimention) == null
					|| dimentionItemCount.getItemsInDimentionAtValue().get(dimention).isEmpty()) {
				continue;
			}
			Map<Integer,Set<String>> mapForDimention = dimentionItemCount.getItemsInDimentionAtValue().get(dimention);
			
			for(Integer coord : mapForDimention.keySet()){
				Set<String> set = mapForDimention.get(coord);
				
				if(set == null || set.isEmpty() || !set.contains(idToRemove))
					continue;
				
				set.remove(idToRemove);
				
				if(set.isEmpty()){
					mapForDimention.remove(coord);
				}else{
					mapForDimention.put(coord, set);
				}	
			}
			
			dimentionItemCount.getItemsInDimentionAtValue().put(dimention, mapForDimention);
			
			
		}
		
		return dimentionItemCount;
	}

}
