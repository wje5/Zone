package com.pinball3d.zone.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerMiner extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/container/miner.png");

	protected ContainerMiner container;

	public GuiContainerMiner(ContainerMiner container) {
		super(container);
		this.container = container;
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;

		drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);

		if (container.getEnergy() > 0) {
			int textureHeight = (int) Math.ceil(14F * container.getEnergy() / container.getMaxEnergy());
			drawTexturedModalRect(offsetX + 16, offsetY + 50 - textureHeight, 176, 14 - textureHeight, 14,
					textureHeight);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String title = I18n.format("tile.miner_" + container.getTier() + ".name");
		fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);
	}
}
