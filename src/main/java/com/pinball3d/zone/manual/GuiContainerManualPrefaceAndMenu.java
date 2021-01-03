package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

public class GuiContainerManualPrefaceAndMenu extends GuiContainerManualBase {
	public GuiContainerManualPrefaceAndMenu(ContainerManual container) {
		super(container);
	}

	@Override
	public void onFlip(boolean flag) {
		if (!flag) {
			mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, 1);
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.preface", x + 18, y + 16);
		drawTextBlock("manual.menu", x2 + 18, y + 16);
	}
}
