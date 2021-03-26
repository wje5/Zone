package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenConnectToNetworkBox extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private int quit;
	private boolean fail;
	private String name;

	public SubscreenConnectToNetworkBox(IHasSubscreen parent, String name) {
		this(parent, name, getDisplayWidth() / 2 - 82, getDisplayHeight() / 2 - 17);
	}

	public SubscreenConnectToNetworkBox(IHasSubscreen parent, String name, int x, int y) {
		super(parent, x, y, 165, 35, false);
		this.name = name;
	}

	public void setData(boolean flag) {
		fail = !flag;
		if (!fail) {
			parent.removeScreen(this);
			return;
		}
		quit = 20;
	}

	@Override
	public void update() {
		super.update();
		if (quit > 0 && fail) {
			quit--;
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 48, 0, 36, 36, 0.5F);
		Util.renderGlowString(I18n.format("sphinx.connect_to_network", name), x + 35, y + 4);
		if (!fail) {
			Util.renderGlowString(I18n.format("sphinx.waiting_for_server"), x + 35, y + 20);
		} else if (fail && quit > 0) {
			Util.renderGlowString(I18n.format("sphinx.waiting_review"), x + 35, y + 20);
		}
		if (fail && quit == 0) {
			dead = true;
		}
	}
}
