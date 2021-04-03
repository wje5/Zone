package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenNewQueue extends Subscreen {
	public SubscreenNewQueue(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNewQueue(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(new TextButton(this, 190, 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.NAME);
		return s;
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
		Util.renderGlowBorder(15, 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.new_queue"), 15, 8);
		Util.renderGlowString(I18n.format("sphinx.set_sphinx_name"), 30, 35);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
