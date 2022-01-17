package com.pinball3d.zone.sphinx.elite.ui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout.Type;

public class PanelHolder extends Subpanel {
	public PanelHolder(EliteMainwindow parent, Subpanel parentPanel) {
		super(parent, parentPanel, new Layout());
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		Iterator<Entry<Component, Pos2i>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Component, Pos2i> e = it.next();
			Component c = e.getKey();
			Pos2i pos = e.getValue();
			EliteRenderHelper.drawLine(0, pos.y + c.getHeight() + c.getMarginDown(), getRenderWidth(),
					pos.y + c.getHeight() + c.getMarginDown(), Color.COMP_BG_DARK);
		}

	}

	private static class Layout implements ILayout {
		@Override
		public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width,
				int height, boolean isPreArrange) {
			Map<Component, Pos2i> m = new LinkedHashMap<Component, Pos2i>();

			int offset = 0;
			List<Entry<Component, List<Object>>> list = new ArrayList<>(origin.entrySet());
			for (int i = 0; i < list.size(); i++) {
				Entry<Component, List<Object>> e = list.get(i);
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
				type = type.getHorizontal();
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
				offset += c.getHeight() + c.getMarginDown() + 1;
			}
			if (isPreArrange) {
				subpanel.setWidth(m.isEmpty() ? 0
						: m.entrySet().stream()
								.mapToInt(e -> e.getKey().getWidth() + e.getValue().x + e.getKey().getMarginRight())
								.max().getAsInt());
				subpanel.setLength(offset);
			}
			return m;
		}
	}
}
