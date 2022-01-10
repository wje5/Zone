package com.pinball3d.zone.sphinx.elite.ui.core.layout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.ILayout;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;

public class PosLayout implements ILayout {
	@Override
	public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange) {
		Map<Component, Pos2i> m = new LinkedHashMap<Component, Pos2i>();
		origin.forEach((c, list) -> {
			Pos2i pos = new Pos2i(0, 0);
			for (Object o : list) {
				if (o instanceof Pos2i) {
					pos = (Pos2i) o;
				}
			}
			if (width - pos.x >= c.getMinWidth()) {
				c.setRenderWidth(Math.min(c.getWidth(), width - pos.x));
			} else {
				c.setRenderWidth(0);
			}
			m.put(c, pos);
		});
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
}
