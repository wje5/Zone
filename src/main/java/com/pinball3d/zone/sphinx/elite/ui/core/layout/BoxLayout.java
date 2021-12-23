package com.pinball3d.zone.sphinx.elite.ui.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.ILayout;

public class BoxLayout implements ILayout {
	@Override
	public Map<Component, Pos2i> arrange(Map<Component, List<Object>> origin, int width) {
		Map<Component, Pos2i> m = new HashMap<Component, Pos2i>();
		int y = 0;
		for (Entry<Component, List<Object>> e : origin.entrySet()) {
			Component c = e.getKey();
			m.put(c, new Pos2i(0, y));
			if (width >= c.getMinWidth()) {
				c.setRenderWidth(Math.min(c.getWidth(), width));
			} else {
				c.setRenderWidth(0);
			}
			y += e.getKey().getHeight();
		}
		return m;
	}
}
