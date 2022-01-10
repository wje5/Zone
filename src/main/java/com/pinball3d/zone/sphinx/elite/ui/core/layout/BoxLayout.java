package com.pinball3d.zone.sphinx.elite.ui.core.layout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.ILayout;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;

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
			Type type = Type.NW;
			for (Object o : e.getValue()) {
				if (o instanceof Type) {
					type = (Type) o;
				}
			}
			if (isVertical) {
				type = type.getHorizontal();
				Component c = e.getKey();
				offset += c.getMarginTop();
				if (type == Type.WEST) {
					c.setRenderWidth(width - c.getMarginLeft() >= c.getMinWidth()
							? Math.min(c.getWidth(), width - c.getMarginLeft())
							: 0);
					m.put(c, new Pos2i(c.getMarginLeft(), offset));
				} else if (type == Type.CENTER) {
					c.setRenderWidth(width >= c.getMinWidth() ? Math.min(c.getWidth(), width) : 0);
					m.put(c, new Pos2i(isPreArrange ? 0 : (width - c.getRenderWidth()) / 2, offset));
				} else {
					c.setRenderWidth(width - c.getMarginRight() >= c.getMinWidth()
							? Math.min(c.getWidth(), width - c.getMarginRight())
							: 0);
					m.put(c, new Pos2i(isPreArrange ? 0 : width - c.getRenderWidth() - c.getMarginRight(), offset));
				}
				offset += c.getHeight() + c.getMarginDown();
			} else {
				type = type.getVertical();
				Component c = e.getKey();
				offset += c.getMarginLeft();
				c.setRenderWidth(width - offset >= c.getMinWidth() ? Math.min(c.getWidth(), width - offset) : 0);

				if (type == Type.NORTH) {
					m.put(c, new Pos2i(offset, c.getMarginTop()));
				} else if (type == Type.CENTER) {
					m.put(c, new Pos2i(offset, isPreArrange ? 0 : (height - c.getHeight()) / 2));
				} else {
					m.put(c, new Pos2i(offset, isPreArrange ? 0 : height - c.getHeight()));
				}
				offset += c.getWidth() + c.getMarginRight();
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
