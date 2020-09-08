package com.pinball3d.zone.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerBurningBox extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/container/burning_box.png");

	protected ContainerBurningBox container;

	public GuiContainerBurningBox(ContainerBurningBox container) {
		super(container);
		this.container = container;
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		if (container.smeltTime > 0) {
			int textureHeight = (int) (container.smeltTime / 14.2857D);
			this.drawTexturedModalRect(offsetX + 81, offsetY + 32 - textureHeight, 176, 14 - textureHeight, 14,
					textureHeight);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String title = I18n.format("container.burning_box");
		fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 5, 0x404040);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
