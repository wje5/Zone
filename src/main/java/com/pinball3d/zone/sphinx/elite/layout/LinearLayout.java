package com.pinball3d.zone.sphinx.elite.layout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.math.ZoneMathHelper;
import com.pinball3d.zone.sphinx.elite.Component;
import com.pinball3d.zone.sphinx.elite.ILayout;
import com.pinball3d.zone.sphinx.elite.Subpanel;
import com.pinball3d.zone.sphinx.elite.layout.BoxLayout.Type;

public class LinearLayout implements ILayout {
	@Override
	public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange) {
		Map<Component, Type> map = new LinkedHashMap<Component, Type>();
		for (Entry<Component, List<Object>> e : origin.entrySet()) {
			if (e.getKey().isHide() || width <= 0) {
				continue;
			}
			Type type = Type.NW;
			for (Object o : e.getValue()) {
				if (o instanceof Type) {
					type = (Type) o;
				}
			}
			map.put(e.getKey(), type);
		}
		Map<Component, Pos2i> m = new LinkedHashMap<Component, Pos2i>();
		int offset = 0, s = 0;
		List<Component> west = new ArrayList<Component>();
		List<Component> center = new ArrayList<Component>();
		List<Component> east = new ArrayList<Component>();
		for (Entry<Component, Type> e : map.entrySet()) {
			Type h = e.getValue().getHorizontal();
			if (h == Type.WEST) {
				west.add(e.getKey());
			} else if (h == Type.CENTER) {
				center.add(e.getKey());
			} else {
				east.add(e.getKey());
			}
		}
		if (isPreArrange) {
			for (Component c : west) {
				offset += c.getMarginLeft();
				c.setRenderWidth(c.getWidth());
				m.put(c, new Pos2i(offset, c.getMarginTop()));
				offset += c.getRenderWidth() + c.getMarginRight();
			}
			for (Component c : center) {
				offset += c.getMarginLeft();
				c.setRenderWidth(c.getWidth());
				m.put(c, new Pos2i(offset, c.getMarginTop()));
				offset += c.getRenderWidth() + c.getMarginRight();
			}
			for (Component c : east) {
				offset += c.getMarginLeft();
				c.setRenderWidth(c.getWidth());
				m.put(c, new Pos2i(offset, c.getMarginTop()));
				offset += c.getRenderWidth() + c.getMarginRight();
			}
		} else {
			int westTotal = west.stream().mapToInt(e -> e.getMarginLeft() + e.getWidth() + e.getMarginRight()).sum();
			int centerTotal = center.stream().mapToInt(e -> e.getMarginLeft() + e.getWidth() + e.getMarginRight())
					.sum();
			int eastTotal = east.stream().mapToInt(e -> e.getMarginLeft() + e.getWidth() + e.getMarginRight()).sum();
			int total = westTotal + centerTotal + eastTotal;
			if (total <= width) {
				for (Component c : west) {
					offset += c.getMarginLeft();
					Type v = map.get(c).getVertical();
					c.setRenderWidth(c.getWidth());
					m.put(c, new Pos2i(offset,
							v == Type.NORTH ? c.getMarginTop()
									: v == Type.CENTER ? (height - c.getHeight()) / 2
											: height - c.getHeight() - c.getMarginDown()));
					offset += c.getWidth() + c.getMarginRight();
				}
				offset = ZoneMathHelper.mid(westTotal, (width - centerTotal) / 2, width - eastTotal - centerTotal);
				for (Component c : center) {
					offset += c.getMarginLeft();
					c.setRenderWidth(c.getWidth());
					m.put(c, new Pos2i(offset, c.getMarginTop()));
					offset += c.getRenderWidth() + c.getMarginRight();
				}
				offset = width - eastTotal;
				for (Component c : east) {
					offset += c.getMarginLeft();
					c.setRenderWidth(c.getWidth());
					m.put(c, new Pos2i(offset, c.getMarginTop()));
					offset += c.getRenderWidth() + c.getMarginRight();
				}
				s = offset;
			} else {
				float scale = width * 1.0F / total;
				westTotal *= scale;
				centerTotal *= scale;
				eastTotal *= scale;
				s = westTotal;
				for (Component c : west) {
					if (westTotal - c.getMarginLeft() - c.getMarginRight() >= c.getMinWidth()) {
						c.setRenderWidth(Math.min(c.getWidth(), westTotal - c.getMarginLeft() - c.getMarginRight()));
					} else {
						c.setRenderWidth(0);
						westTotal = 0;
					}
					m.put(c, new Pos2i(offset + c.getMarginLeft(), c.getMarginTop()));
					offset += c.getMarginLeft() + c.getRenderWidth() + c.getMarginRight();
				}
				offset = s;
				s += centerTotal;
				for (Component c : center) {
					if (centerTotal - c.getMarginLeft() - c.getMarginRight() >= c.getMinWidth()) {
						c.setRenderWidth(Math.min(c.getWidth(), centerTotal - c.getMarginLeft() - c.getMarginRight()));
					} else {
						c.setRenderWidth(0);
						centerTotal = 0;
					}
					m.put(c, new Pos2i(offset + c.getMarginLeft(), c.getMarginTop()));
					offset += c.getMarginLeft() + c.getRenderWidth() + c.getMarginRight();
				}
				offset = s;
				s += eastTotal;
				for (Component c : east) {
					if (eastTotal - c.getMarginLeft() - c.getMarginRight() >= c.getMinWidth()) {
						c.setRenderWidth(Math.min(c.getWidth(), eastTotal - c.getMarginLeft() - c.getMarginRight()));
					} else {
						c.setRenderWidth(0);
						eastTotal = 0;
					}
					m.put(c, new Pos2i(offset + c.getMarginLeft(), c.getMarginTop()));
					offset += c.getMarginLeft() + c.getRenderWidth() + c.getMarginRight();
				}
			}
		}
		if (isPreArrange) {
			subpanel.setWidth(s);
			subpanel.setLength(m.isEmpty() ? 0
					: m.entrySet().stream()
							.mapToInt(e -> e.getKey().getHeight() + e.getValue().y + e.getKey().getMarginDown()).max()
							.getAsInt());
		}
		return m;
	}
}
