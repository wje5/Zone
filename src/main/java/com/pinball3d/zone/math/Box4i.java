package com.pinball3d.zone.math;

import com.google.common.base.MoreObjects;

public class Box4i {
	public final int x, y, width, height;

	public Box4i(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Box4i add(int width, int height) {
		return new Box4i(x, y, this.width + width, this.height + height);
	}

	public Box4i move(int moveX, int moveY) {
		return new Box4i(x + moveX, y + moveY, width, height);
	}

	public Box4i intersection(Box4i box) {
		if (this.equals(box)) {
			return new Box4i(x, y, width, height);
		}
		int x2 = x + width;
		int bx2 = box.x + box.width;
		int y2 = y + height;
		int by2 = box.y + box.height;
		if (x >= bx2 || box.x >= x2 || y >= by2 || box.y >= y2) {
			return null;
		}
		int ix = Math.max(x, box.x);
		int iy = Math.max(y, box.y);
		return new Box4i(ix, iy, Math.min(x2, bx2) - ix, Math.min(y2, by2) - iy);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Box4i)) {
			return false;
		}
		Box4i box = (Box4i) o;
		return box.x == x && box.y == y && box.width == width && box.height == height;
	}

	@Override
	public int hashCode() {
		return (((x * 31) + y) * 31 + width) * 31 + height;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", x).add("y", y).add("w", width).add("h", height).toString();
	}
}
