package com.pinball3d.zone.sphinx.elite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;

public class EliteGuiContainer extends GuiContainer {
	public EliteGuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		xSize = Minecraft.getMinecraft().displayWidth;
		ySize = Minecraft.getMinecraft().displayHeight;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50F);
		EliteRenderHelper.drawRect(0, 0, width, height, 0xFF2F2F2F);
		EliteRenderHelper.renderText(100, 100, "造", 0xFFFF0000);
		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	}
}
