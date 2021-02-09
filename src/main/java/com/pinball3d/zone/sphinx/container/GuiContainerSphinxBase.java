package com.pinball3d.zone.sphinx.container;

import java.util.Set;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerSphinxBase extends GuiContainerNetworkBase {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation TEXTURE_3 = new ResourceLocation("zone:textures/gui/sphinx/icons_3.png");
	public static final ResourceLocation TEXTURE_NO_NETWORK = new ResourceLocation(
			"zone:textures/gui/sphinx/no_network.png");

	public GuiContainerSphinxBase(ContainerSphinxBase container) {
		super(container);
	}

	@Override
	protected void draw(int mouseX, int mouseY, float partialTicks) {
		super.draw(mouseX, mouseY, partialTicks);
		if (ConnectHelperClient.getInstance().isConnected()) {
			if (subscreens.isEmpty()) {
				dragMap(mouseX, mouseY, partialTicks);
			}
			MapHandler.draw(ConnectHelperClient.getInstance().getNetworkPos(), width, height);
		} else {
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
			Util.drawTexture(TEXTURE_NO_NETWORK, width / 2 - 32, height / 2 - 32, 256, 256, 0.25F);
			String text = I18n.format("sphinx.no_network");
			fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, height / 2 + 45,
					0xFFE0E0E0);
		}
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.NETWORKPOS);
		set.add(Type.MAP);
		set.add(Type.PACK);
		return set;
	}

	public void dragMap(int mouseX, int mouseY, float partialTicks) {
		float sensitive = 5.0F;
		if (mouseX <= 1) {
			partialMoveX -= partialTicks * sensitive;
		}
		if (mouseX >= width - 1) {
			partialMoveX += partialTicks * sensitive;
		}
		if (mouseY <= 1) {
			partialMoveY -= partialTicks * sensitive;
		}
		if (mouseY >= height - 1) {
			partialMoveY += partialTicks * sensitive;
		}
		MapHandler.dragMap((int) partialMoveX, (int) partialMoveY);
		onDragMap(mouseX, mouseY, (int) partialMoveX, (int) partialMoveY);
		partialMoveX -= (int) partialMoveX;
		partialMoveY -= (int) partialMoveY;
	}

	public void onDragMap(int mouseX, int mouseY, int moveX, int moveY) {

	}
}
