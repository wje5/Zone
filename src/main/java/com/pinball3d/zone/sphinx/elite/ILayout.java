package com.pinball3d.zone.sphinx.elite;

import java.util.List;
import java.util.Map;

import com.pinball3d.zone.math.Pos2i;

public interface ILayout {
	public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width, int height,
			boolean isPreArrange);
}
