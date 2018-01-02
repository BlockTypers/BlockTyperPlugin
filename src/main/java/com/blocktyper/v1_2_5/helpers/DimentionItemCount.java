package com.blocktyper.v1_2_5.helpers;

import java.util.Map;
import java.util.Set;

public class DimentionItemCount {
	public DimentionItemCount() {

	}

	private Map<String, Map<String, Map<Integer, Set<String>>>> itemsInDimentionAtValue;

	public Map<String, Map<String, Map<Integer, Set<String>>>> getItemsInDimentionAtValue() {
		return itemsInDimentionAtValue;
	}

	public void setItemsInDimentionAtValue(
			Map<String, Map<String, Map<Integer, Set<String>>>> itemsInDimentionAtValue) {
		this.itemsInDimentionAtValue = itemsInDimentionAtValue;
	}
}
