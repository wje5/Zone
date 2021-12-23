package com.pinball3d.zone.math;

import com.google.common.base.MoreObjects;

public class Pos2i {
	public final int x, y;

	public Pos2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Pos2i add(int x, int y) {
		return new Pos2i(this.x + x, this.y + y);
	}

	public Pos2i add(Pos2i pos) {
		return add(pos.x, pos.y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Pos2i)) {
			return false;
		}
		Pos2i pos = (Pos2i) o;
		return pos.x == x && pos.y == y;
	}

	@Override
	public int hashCode() {
		return (x * 31) + y;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", x).add("y", y).toString();
	}

}
