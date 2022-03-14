package com.pinball3d.zone.inventory;

import java.io.IOException;
import java.util.Arrays;

import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.network.elite.MessageCableConfig;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig.ItemIOType;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerCable2 extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/container/cable_2.png");
	private boolean enableEnergy, hasData;
	private ItemIOType itemIOType;
	protected ContainerCable2 container;

	public GuiContainerCable2(ContainerCable2 container) {
		super(container);
		this.container = container;
		xSize = 176;
		ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;
		buttonList.add(new GuiButton(0, offsetX + 34, offsetY + 51, 19, 19, "") {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (visible) {
					GlStateManager.color(1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(TEXTURE);
					hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
					if (hasData) {
						drawTexturedModalRect(x, y, enableEnergy ? 207 : 226, hovered ? 57 : 19, width, height);
					}
				}
			}
		});
		buttonList.add(new GuiButton(1, offsetX + 55, offsetY + 51, 19, 19, "") {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (visible) {
					GlStateManager.color(1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(TEXTURE);
					hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
					if (hasData) {
						drawTexturedModalRect(x, y,
								itemIOType == ItemIOType.DISABLE ? 188 : 188 + itemIOType.ordinal() * 19,
								(itemIOType == ItemIOType.DISABLE ? 19 : 0) + (hovered ? 38 : 0), width, height);
					}
				}
			}
		});
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			enableEnergy = !enableEnergy;
			return;
		case 1:
			int index = itemIOType.ordinal() + 1;
			itemIOType = ItemIOType.values()[index >= ItemIOType.values().length ? 0 : index];
		}
		super.actionPerformed(button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		if (!hasData) {
			enableEnergy = container.energyTransmit;
			itemIOType = container.itemIOType;
			hasData = true;
		}
		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;
		drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
		if (container.networkEnergy > 0) {
			int textureHeight = (int) Math
					.ceil(Math.min(52F, 52F * container.networkEnergy / container.networkMaxEnergy));
			drawTexturedModalRect(offsetX + 17, offsetY + 69 - textureHeight, 176, 52 - textureHeight, 12,
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
		if (mc.player.inventory.getItemStack().isEmpty() && x - offsetX >= 17 && x - offsetX <= 29 && y - offsetY >= 17
				&& y - offsetY <= 69) {
			drawHoveringText(Arrays.asList(
					Util.formatEnergy(container.networkEnergy) + "/" + Util.formatEnergy(container.networkMaxEnergy)),
					x, y);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String title = I18n.format("tile.cable_2.name");
		fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);

		fontRenderer.drawString(Util.formatEnergy(container.networkInput), 44, 16, 0x404040);
		fontRenderer.drawString(Util.formatEnergy(container.networkOutput), 44, 27, 0x404040);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		CableConfig config = container.tileEntity.getConfig(container.facing);
		config.setEnergyTransmit(enableEnergy);
		config.setItemIOType(itemIOType);
		NetworkHandler.instance.sendToServer(
				new MessageCableConfig(new WorldPos(container.tileEntity), container.facing, enableEnergy, itemIOType));
	}
}
