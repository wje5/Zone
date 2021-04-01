package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.component.ScrollingListNetwork;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenNetworkConfig extends Subscreen {
	public ScrollingListNetwork list;

	public SubscreenNetworkConfig(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, WorldPos pos) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, pos);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, int x, int y) {
		this(parent, x, y, WorldPos.ORIGIN);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, int x, int y, WorldPos pos) {
		super(parent, x, y, 300, 200, true);
		addComponent(list = new ScrollingListNetwork(this, 16, 24, 268, 170, pos));
	}

	public void refresh() {
		list.refresh();
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -5, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, -5, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(44, 0, 255, 44, 0x2F000000);
		Gui.drawRect(0, 44, 300, 155, 0x2F000000);
		Gui.drawRect(44, 155, 255, 200, 0x2F000000);
		Util.renderGlowHorizonLine(10, 20, 280);
		Gui.drawRect(16, 24, 284, 194, 0x651CC3B5);
		Util.renderGlowString(I18n.format("sphinx.network_config"), 15, 8);
		Util.renderGlowBorder(15, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
