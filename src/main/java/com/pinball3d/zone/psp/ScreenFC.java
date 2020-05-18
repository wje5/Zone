package com.pinball3d.zone.psp;

import com.pinball3d.zone.sphinx.Util;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ScreenFC extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/psp/border.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("zone:textures/gui/psp/border_2.png");

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Util.drawTexture(TEXTURE, 0, 0, 256, 240);
		Util.drawTexture(TEXTURE_2, 256, 0, 171, 240);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
