package com.blocktyper.helpers;

import java.util.List;

public interface IClickedBlockHelper {
	List<String> getMatchesInDimentionItemCount(DimentionItemCount dimentionItemCount, String world, int x, int y, int z);
	DimentionItemCount removeIdFromDimentionItemCount(String idToRemove, DimentionItemCount dimentionItemCount);
}
