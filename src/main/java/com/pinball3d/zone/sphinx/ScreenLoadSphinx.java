package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ScreenLoadSphinx extends GuiScreen {
	public TEProcessingCenter tileentity;

	public ScreenLoadSphinx(TEProcessingCenter te) {
		tileentity = te;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!tileentity.isLoading()) {
			mc.displayGuiScreen(new ScreenSphinxController(tileentity));
			return;
		}
		Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
		Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/sphinx_load_0.png"), width / 2 - 29,
				height / 2 - 32, 0, 0, 256, 256, 0.25F);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
