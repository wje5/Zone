package com.pinball3d.zone.sphinx.elite.ui.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.ILayout;

public class PosLayout implements ILayout {
	@Override
	public Map<Component, Pos2i> arrange(Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange) {
		Map<Component, Pos2i> m = new HashMap<Component, Pos2i>();
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
		return m;
	}
}
