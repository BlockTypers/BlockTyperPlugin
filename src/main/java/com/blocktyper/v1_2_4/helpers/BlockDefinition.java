package com.blocktyper.v1_2_4.helpers;

import java.io.Serializable;

public class BlockDefinition implements Serializable {
	private static final long serialVersionUID = 201702052217L;
	
	
	private Coord coord;
	private ComplexMaterial complexMaterial;
	public Coord getCoord() {
		return coord;
	}
	public void setCoord(Coord coord) {
		this.coord = coord;
	}
	public ComplexMaterial getComplexMaterial() {
		return complexMaterial;
	}
	public void setComplexMaterial(ComplexMaterial complexMaterial) {
		this.complexMaterial = complexMaterial;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complexMaterial == null) ? 0 : complexMaterial.hashCode());
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
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
		BlockDefinition other = (BlockDefinition) obj;
		if (complexMaterial == null) {
			if (other.complexMaterial != null)
				return false;
		} else if (!complexMaterial.equals(other.complexMaterial))
			return false;
		if (coord == null) {
			if (other.coord != null)
				return false;
		} else if (!coord.equals(other.coord))
			return false;
		return true;
	}
	
	
	
	
}
