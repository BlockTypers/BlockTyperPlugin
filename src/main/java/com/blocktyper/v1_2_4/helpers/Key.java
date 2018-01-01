package com.blocktyper.v1_2_4.helpers;

public class Key {
	String val;

	public Key(String val) {
		super();
		this.val = val;
	}

	public Key __(String subKey) {
		val = val + "." + subKey;
		return this;
	}

	public String end(String subKey) {
		return val + "." + subKey;
	}

	public String getVal() {
		return val;
	}
	
	
}
