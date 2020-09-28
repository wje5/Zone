package com.pinball3d.zone.manual;

import net.minecraft.client.Minecraft;

public class ScreenManualSphinxSystem5 extends ScreenManualBase {
	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualSphinxSystem4());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_9", x + 18, y + 16);
	}
}
