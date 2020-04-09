package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class SubscreenQuitWizard extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/sphinx/connect_to_network.png");

	public SubscreenQuitWizard(IParent parent) {
		this(parent, parent.getWidth() / 2 - 80, parent.getHeight() / 2 - 45);
	}

	public SubscreenQuitWizard(IParent parent, int x, int y) {
		super(parent, x, y, 160, 90, false);
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawBorder(x, y, width, height, 1, 0xFFFF0000);
//		parent.getFontRenderer().drawString(I18n.format("sphinx.connect_to_network", name), x + 35, y + 4, 0xFF1ECCDE);
	}
}
