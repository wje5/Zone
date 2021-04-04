package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;

import net.minecraft.client.gui.Gui;

public class QueueChart extends Component {
	public QueueChart(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);
		Gui.drawRect(0, 0, width, height, 0xFFFFFFFF);
	}
}
