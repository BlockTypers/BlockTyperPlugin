package com.blocktyper.v1_1_8.helpers;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ComplexMaterial implements Serializable {
	private static final long serialVersionUID = 201702052217L;
	
	String mat;
	Byte data;
	
	public ComplexMaterial(){
		
	}
	
	public ComplexMaterial(Material material, Byte data) {
		super();
		this.mat = material != null ? material.toString() : null;
		setData(data);
	}
	
	@SuppressWarnings("deprecation")
	public ComplexMaterial(Block block) {
		super();
		this.mat = block != null && block.getType() != null ? block.getType().toString() : null;
		setData(block != null ? block.getData() : 0);
	}
	
	@SuppressWarnings("deprecation")
	public ComplexMaterial(ItemStack item) {
		super();
		this.mat = item != null && item.getType() != null ? item.getType().toString() : null;
		setData(item != null && item.getData() != null ? item.getData().getData() : 0);
	}
	
	public Material getMaterial() {
		return Material.matchMaterial(mat);
	}
	public void setMaterial(Material material) {
		this.mat = material != null ? material.toString() : null;
	}
	public String getMat() {
		return mat;
	}
	public void setMat(String mat) {
		this.mat = mat;
	}
	public Byte getData() {
		return data == null ? 0 : data;
	}
	public void setData(Byte data) {
		this.data = data == null ? 0 : data;
	}
	
	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((mat == null) ? 0 : mat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ComplexMaterial other = (ComplexMaterial) obj;
		if (getData() == null) {
			if (other.getData() != null){
				return false;
			}
		} else if (!getData().equals(other.getData())){
			return false;
		}
		if (mat == null) {
			if (other.mat != null){
				return false;
			}
		} else if (!mat.equals(other.mat)){
			return false;
		}
		return true;
	}





	private static String DELIMINATOR = "-";
	
	@Override
	public String toString() {
		return mat + DELIMINATOR + (data != null ? data : 0);
	}
	
	public static ComplexMaterial fromString(String val) {
		if(val == null){
			return null;
		}
		
		Material material = null;
		Byte data = 0;
		if(val.contains(DELIMINATOR)){
			material = Material.matchMaterial(val.substring(0,val.indexOf(DELIMINATOR)));
			data = Byte.parseByte(val.substring(val.indexOf(DELIMINATOR) + 1));
		}else{
			material =  Material.matchMaterial(val);
		}
		
		ComplexMaterial complexMaterial = new ComplexMaterial(material, data);
		return complexMaterial;
	}
	
	
	
}
