package com.pinball3d.zone.sphinx.elite.layout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Component;
import com.pinball3d.zone.sphinx.elite.ILayout;
import com.pinball3d.zone.sphinx.elite.Subpanel;

public class BoxLayout implements ILayout {
	private boolean isVertical;

	public BoxLayout(boolean isVertical) {
		this.isVertical = isVertical;
	}

	@Override
	public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange) {
		Map<Component, Pos2i> m = new LinkedHashMap<Component, Pos2i>();
		int offset = 0;
		for (Entry<Component, List<Object>> e : origin.entrySet()) {
			Component c = e.getKey();
			if (c.isHide() || width <= 0) {
				continue;
			}
			Type type = Type.NW;
			for (Object o : e.getValue()) {
				if (o instanceof Type) {
					type = (Type) o;
				}
			}
			if (isVertical) {
				type = type.getHorizontal();
				offset += c.getMarginTop();
				c.setRenderWidth(width - c.getMarginLeft() - c.getMarginRight() >= c.getMinWidth()
						? Math.min(c.getWidth(), width - c.getMarginLeft() - c.getMarginRight())
						: 0);
				if (type == Type.WEST) {
					m.put(c, new Pos2i(c.getMarginLeft(), offset));
				} else if (type == Type.CENTER) {
					m.put(c, new Pos2i(isPreArrange ? c.getMarginLeft() : (width - c.getRenderWidth()) / 2, offset));
				} else {
					m.put(c, new Pos2i(
							isPreArrange ? c.getMarginLeft() : width - c.getRenderWidth() - c.getMarginRight(),
							offset));
				}
				offset += c.getHeight() + c.getMarginDown();
			} else {
				type = type.getVertical();
				offset += c.getMarginLeft();
				c.setRenderWidth(width - offset - c.getMarginRight() >= c.getMinWidth()
						? Math.min(c.getWidth(), width - offset - c.getMarginRight())
						: 0);
				if (type == Type.NORTH) {
					m.put(c, new Pos2i(offset, c.getMarginTop()));
				} else if (type == Type.CENTER) {
					m.put(c, new Pos2i(offset, isPreArrange ? c.getMarginTop() : (height - c.getHeight()) / 2));
				} else {
					m.put(c, new Pos2i(offset,
							isPreArrange ? c.getMarginTop() : height - c.getHeight() - c.getMarginDown()));
				}
				offset += c.getRenderWidth() + c.getMarginRight();
			}
		}
		if (isPreArrange) {
			subpanel.setWidth(m.isEmpty() ? 0
					: m.entrySet().stream()
							.mapToInt(e -> e.getKey().getWidth() + e.getValue().x + e.getKey().getMarginRight()).max()
							.getAsInt());
			subpanel.setLength(m.isEmpty() ? 0
					: m.entrySet().stream()
							.mapToInt(e -> e.getKey().getHeight() + e.getValue().y + e.getKey().getMarginDown()).max()
							.getAsInt());
		}
		return m;
	}

	public static enum Type {
		NORTH, SOUTH, WEST, EAST, CENTER, NW, NE, SW, SE;

		public Type getVertical() {
			switch (this) {
			case NORTH:
			case NW:
			case NE:
				return NORTH;
			case CENTER:
			case WEST:
			case EAST:
				return CENTER;
			case SOUTH:
			case SW:
			case SE:
				return SOUTH;
			}
			return null;
		}

		public Type getHorizontal() {
			switch (this) {
			case WEST:
			case NW:
			case SW:
				return WEST;
			case CENTER:
			case NORTH:
			case SOUTH:
				return CENTER;
			case EAST:
			case NE:
			case SE:
				return EAST;
			}
			return null;
		}
	}
}
