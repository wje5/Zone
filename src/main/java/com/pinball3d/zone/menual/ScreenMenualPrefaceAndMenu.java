package com.pinball3d.zone.menual;

import net.minecraft.client.Minecraft;

public class ScreenMenualPrefaceAndMenu extends ScreenMenualBase {
	@Override
	public void onFlip(boolean flag) {
		if (!flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenMenualToolAndMaterial());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("menual.preface", x + 18, y + 16);
		drawTextBlock("menual.menu", x2 + 18, y + 16);
	}
}
