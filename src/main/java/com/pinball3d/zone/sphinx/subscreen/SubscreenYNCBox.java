package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenYNCBox extends Subscreen {
	public String title, text;

	public SubscreenYNCBox(IHasSubscreen parent, String title, String text, Runnable yes, Runnable no) {
		this(parent, getDisplayWidth() / 2 - 75, getDisplayHeight() / 2 - 50, title, text, yes, no);
	}

	public SubscreenYNCBox(IHasSubscreen parent, int x, int y, String title, String text, Runnable yes, Runnable no) {
		super(parent, x, y, 150, 100, true);
		addComponent(new TextButton(this, 15, 80, I18n.format("sphinx.yes"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
			if (yes != null) {
				yes.run();
			}
		}));
		addComponent(new TextButton(this, 55, 80, I18n.format("sphinx.no"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
			if (no != null) {
				no.run();
			}
		}));
		addComponent(new TextButton(this, 95, 80, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
		}));
		this.title = title;
		this.text = text;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -2, -2, 0, 0, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, 128, -2, 99, 0, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, -2, 79, 0, 99, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, 128, 79, 99, 99, 99, 99, 0.25F);
		Gui.drawRect(22, 0, 128, 22, 0x2F000000);
		Gui.drawRect(0, 22, 150, 79, 0x2F000000);
		Gui.drawRect(22, 79, 128, 101, 0x2F000000);
		Util.renderGlowHorizonLineThin(5, 10, 140);
		Gui.drawRect(8, 12, 142, 97, 0x651CC3B5);
		Util.renderGlowString(title, 7, 2);
		Util.renderSplitGlowString(text, 15, 15, 120);
		Util.renderGlowBorder(7, 12, 135, 86);
	}
}
