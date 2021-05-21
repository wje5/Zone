package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.MouseHandler;
import com.pinball3d.zone.sphinx.elite.PanelGroup;

import net.minecraft.client.resources.I18n;

public class PanelMap extends Panel {
	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, I18n.format("elite.panel.map"));
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
//		MouseHandler.changeMouse(MouseType.RESIZE_S);
		MouseHandler.changeMouse(null);
		super.doRender(mouseX, mouseY);
	}
}
