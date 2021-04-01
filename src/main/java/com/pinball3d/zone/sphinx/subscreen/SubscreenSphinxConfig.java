package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.ScrollingList;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenSphinxConfig extends Subscreen {
	public ScrollingList list;

	public SubscreenSphinxConfig(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenSphinxConfig(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		list = new ScrollingList(this, 16, 24, 268, 170);
		list.addListBar(I18n.format("sphinx.change_sphinx_name"), () -> {
			parent.removeScreen(SubscreenSphinxConfig.this);
			parent.putScreen(new SubscreenChangeName(parent));
		}, 0, 172, 16, 15, 1.0F);
		addComponent(list);
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
		Util.renderGlowString(I18n.format("sphinx.sphinx_config"), 15, 8);
		Util.renderGlowBorder(15, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
