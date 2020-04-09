package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenConnectToNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/sphinx/connect_to_network.png");
	private String input = "";
	public String name;

	public SubscreenConnectToNetwork(IParent parent, String name) {
		this(parent, name, parent.getWidth() / 2 - 82, parent.getHeight() / 2 - 17);
	}

	public SubscreenConnectToNetwork(IParent parent, String name, int x, int y) {
		super(parent, x + 165 + parent.getXOffset() > displayWidth ? displayWidth - 165 - parent.getXOffset() : x,
				y + 35 + parent.getYOffset() > displayHeight ? displayHeight - 35 - parent.getYOffset() : y, 165, 35,
				false);
		this.name = name;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 36, 36, 0.5F);
		parent.getFontRenderer().drawString(I18n.format("sphinx.connect_to_network", name), x + 35, y + 4, 0xFF1ECCDE);
		for (int i = 0; i < input.length(); i++) {
			Util.drawTexture(TEXTURE, x + 35 + i * 16, y + 18, 36, 0, 21, 21, 0.5F);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		typedChar = Character.toLowerCase(typedChar);
		if (!subscreens.empty()) {
			subscreens.peek().keyTyped(typedChar, keyCode);
		} else {
			if (Util.isValidChar(typedChar, 3)) {
				input += typedChar;
				if (input.length() >= 8) {
					parent.quitScreen(this);
				}
			}
		}
	}
}
