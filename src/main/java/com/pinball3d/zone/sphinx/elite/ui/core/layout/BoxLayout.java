package com.pinball3d.zone.sphinx.elite.ui.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.ILayout;

public class BoxLayout implements ILayout {
	private boolean isVertical;

	public BoxLayout(boolean isVertical) {
		this.isVertical = isVertical;
	}

	@Override
	public Map<Component, Pos2i> arrange(Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange) {
		Map<Component, Pos2i> m = new HashMap<Component, Pos2i>();
		int offset = 0;
		for (Entry<Component, List<Object>> e : origin.entrySet()) {
			Type type = Type.NW;
			for (Object o : e.getValue()) {
				if (o instanceof Type) {
					type = (Type) o;
				}
			}
			if (isVertical) {
				Component c = e.getKey();
				if (width >= c.getMinWidth()) {
					c.setRenderWidth(Math.min(c.getWidth(), width));
				} else {
					c.setRenderWidth(0);
				}
				if (isPreArrange) {
					m.put(c, new Pos2i(type.offset, offset));
				} else if (type == Type.NW) {
					m.put(c, new Pos2i(0, offset));
				} else if (type == Type.CENTER) {
					m.put(c, new Pos2i((width - c.getRenderWidth()) / 2, offset));
				} else if (type == Type.SE) {
					m.put(c, new Pos2i(width - c.getRenderWidth(), offset));
				} else {
					m.put(c, new Pos2i(type.offset, offset));
				}
				offset += c.getHeight();
			} else {
				Component c = e.getKey();
				if (width - offset >= c.getMinWidth()) {
					c.setRenderWidth(Math.min(c.getWidth(), width - offset));
				} else {
					c.setRenderWidth(0);
				}
				if (isPreArrange) {
					m.put(c, new Pos2i(offset, type.offset));
				} else if (type == Type.NW) {
					m.put(c, new Pos2i(offset, 0));
				} else if (type == Type.CENTER) {
					m.put(c, new Pos2i(offset, (height - c.getHeight()) / 2));
				} else if (type == Type.SE) {
					m.put(c, new Pos2i(offset, height - c.getHeight()));
				} else {
					m.put(c, new Pos2i(offset, type.offset));
				}
				offset += c.getWidth();

			}
		}
		return m;
	}

	public static class Type {
		public final int offset;

		public static final Type NW = new Type(0);
		public static final Type CENTER = new Type(0);
		public static final Type SE = new Type(0);

		public Type(int offset) {
			this.offset = offset;
		}
	}
}
