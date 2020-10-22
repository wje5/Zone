package com.pinball3d.zone.manual;

import net.minecraft.client.Minecraft;

public class ScreenManualSphinxSystem extends ScreenManualBase {
	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualLatheAndFormingPress());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualSphinxSystem2());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system", x + 18, y + 16);
		drawTextBlock("manual.sphinx_system_2", x2 + 18, y + 16);
	}
}
