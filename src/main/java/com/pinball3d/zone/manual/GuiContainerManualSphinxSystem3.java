package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

public class GuiContainerManualSphinxSystem3 extends GuiContainerManualBase {
	public GuiContainerManualSphinxSystem3(ContainerManualSphinxSystem3 container) {
		super(container);
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 10 : 12);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_5", x + 18, y + 16);
		drawTextBlock("manual.sphinx_system_6", x2 + 18, y + 16);
	}
}
