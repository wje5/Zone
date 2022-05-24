package com.pinball3d.zone.inventory;

import java.util.Arrays;

import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerDynavoltRestrainer extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/container/dynavolt_restrainer.png");

	protected ContainerDynavoltRestrainer container;

	public GuiContainerDynavoltRestrainer(ContainerDynavoltRestrainer container) {
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
			int textureHeight = (int) Math.ceil(52F * container.getEnergy() / container.getMaxEnergy());
			drawTexturedModalRect(offsetX + 112, offsetY + 69 - textureHeight, 176, 52 - textureHeight, 12,
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
	protected void renderHoveredToolTip(int x, int y) {
		super.renderHoveredToolTip(x, y);
		int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;
		if (mc.player.inventory.getItemStack().isEmpty() && x - offsetX >= 112 && x - offsetX <= 124
				&& y - offsetY >= 17 && y - offsetY <= 69) {
			drawHoveringText(Arrays.asList(
					Util.formatEnergy(container.getEnergy()) + "/" + Util.formatEnergy(container.getMaxEnergy())), x,
					y);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String title = I18n.format("tile.dynavolt_restrainer.name");
		fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);
	}
}
