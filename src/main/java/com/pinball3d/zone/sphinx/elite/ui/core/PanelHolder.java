package com.pinball3d.zone.sphinx.elite.ui.core;

import java.util.HashMap;
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
		super(parent, parentPanel, new SingleColumnLayout());
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		components.forEach((c, pos) -> {
			EliteRenderHelper.drawLine(0, pos.y + c.getHeight(), getRenderWidth(), pos.y + c.getHeight(),
					Color.COMP_BG_DARK);
		});
	}

	public static class SingleColumnLayout implements ILayout {
		@Override
		public Map<Component, Pos2i> arrange(Map<Component, List<Object>> origin, int width, int height,
				boolean isPreArrange) {
			Map<Component, Pos2i> m = new HashMap<Component, Pos2i>();
			int offset = 0;
			for (Entry<Component, List<Object>> e : origin.entrySet()) {
				Type type = Type.NW;
				boolean flag = false;
				for (Object o : e.getValue()) {
					if (o instanceof Type) {
						type = (Type) o;
					} else if (o instanceof Boolean) {
						flag = (Boolean) o;
					}
				}

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
				offset += c.getHeight() + 1;
			}
			return m;
		}
	}
}
