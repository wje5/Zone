package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

public class GuiContainerManualSphinxSystem5 extends GuiContainerManualBase {
	public GuiContainerManualSphinxSystem5(ContainerManual container) {
		super(container);
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, 12);
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_9", x + 18, y + 16);
	}
}
