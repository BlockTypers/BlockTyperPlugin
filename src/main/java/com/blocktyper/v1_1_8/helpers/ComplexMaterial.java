package com.blocktyper.v1_1_8.helpers;

import org.bukkit.Material;

public class ComplexMaterial{
	Material material;
	Byte data;
	
	public ComplexMaterial(Material material, Byte data) {
		super();
		this.material = material;
		this.data = data;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Byte getData() {
		return data;
	}
	public void setData(Byte data) {
		this.data = data;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComplexMaterial other = (ComplexMaterial) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (material != other.material)
			return false;
		return true;
	}
	
}
