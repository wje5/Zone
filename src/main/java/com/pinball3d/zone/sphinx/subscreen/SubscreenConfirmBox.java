package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenConfirmBox extends Subscreen {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public String title, text;

	public SubscreenConfirmBox(IHasSubscreen parent, String title, String text, Runnable confirm) {
		this(parent, getDisplayWidth() / 2 - 75, getDisplayHeight() / 2 - 50, title, text, confirm);
	}

	public SubscreenConfirmBox(IHasSubscreen parent, int x, int y, String title, String text, Runnable confirm) {
		super(parent, x, y, 150, 100, true);
		components.add(new TextButton(this, this.x + 20, this.y + 80, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenConfirmBox.this);
			if (confirm != null) {
				confirm.run();
			}
		}));
		components.add(new TextButton(this, this.x + 90, this.y + 80, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenConfirmBox.this);
		}));
		this.title = title;
		this.text = text;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x - 2, y - 2, 0, 0, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x + 128, y - 2, 99, 0, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x - 2, y + 79, 0, 99, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x + 128, y + 79, 99, 99, 99, 99, 0.25F);
		Gui.drawRect(x + 22, y, x + 128, y + 22, 0x2F000000);
		Gui.drawRect(x, y + 22, x + 150, y + 79, 0x2F000000);
		Gui.drawRect(x + 22, y + 79, x + 128, y + 101, 0x2F000000);
		Util.renderGlowHorizonLineThin(x + 5, y + 10, 140);
		Gui.drawRect(x + 8, y + 12, x + 142, y + 97, 0x651CC3B5);
		Util.renderGlowString(title, x + 7, y + 2);
		Util.renderSplitGlowString(text, x + 15, y + 15, 120);
		Util.renderGlowBorder(x + 7, y + 12, 135, 86);
	}
}
