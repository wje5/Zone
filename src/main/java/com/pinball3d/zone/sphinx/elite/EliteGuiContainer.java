package com.pinball3d.zone.sphinx.elite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class EliteGuiContainer extends GuiContainer {
	public static final ResourceLocation ELITE = new ResourceLocation("zone:textures/gui/elite/elite.png");

	public EliteGuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		xSize = Minecraft.getMinecraft().displayWidth;
		ySize = Minecraft.getMinecraft().displayHeight;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50F);
		EliteRenderHelper.drawRect(0, 0, width, height, 0xFF282828);
		EliteRenderHelper.drawRect(0, 0, width, 7, 0xFF535353);
		FontHelper.renderText(FontHelper.getStringWidth("Rendering"), 2, "视图", 0xFFC7C7C7);
		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

	}
}
