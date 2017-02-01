package com.blocktyper.v1_1_8.helpers;

import org.bukkit.block.Block;

public class Coord {
	Integer x;
	Integer y;
	Integer z;
	
	
	public Coord() {
		super();
	}
	
	public Coord(Integer x, Integer y, Integer z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord(Block block) {
		super();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}
	
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(Integer z) {
		this.z = z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		result = prime * result + ((z == null) ? 0 : z.hashCode());
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
		Coord other = (Coord) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		if (z == null) {
			if (other.z != null)
				return false;
		} else if (!z.equals(other.z))
			return false;
		return true;
	}
	
	
	
	
}
