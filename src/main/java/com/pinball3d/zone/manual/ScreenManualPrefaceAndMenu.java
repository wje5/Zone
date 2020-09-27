package com.pinball3d.zone.manual;

import net.minecraft.client.Minecraft;

public class ScreenManualPrefaceAndMenu extends ScreenManualBase {
	@Override
	public void onFlip(boolean flag) {
		if (!flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualToolAndMaterial());
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
