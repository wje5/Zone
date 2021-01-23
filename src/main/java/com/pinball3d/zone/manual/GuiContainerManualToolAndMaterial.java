package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

public class GuiContainerManualToolAndMaterial extends GuiContainerManualBase {
	public GuiContainerManualToolAndMaterial(ContainerManualToolAndMaterial container) {
		super(container);
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 0 : 2);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.tool", x + 18, y + 16);
		drawTextBlock("manual.material", x2 + 18, y + 16);
		drawFrame(x + 18, y + 47);
		drawFrame(x + 36, y + 47);
		drawFrame(x + 54, y + 47);
		drawFrame(x + 72, y + 47);
		drawFrame(x + 164, y + 38);
		drawFrame(x + 182, y + 38);
		drawFrame(x + 200, y + 38);
		drawFrame(x + 218, y + 38);
		drawFrame(x + 236, y + 38);
	}
}
