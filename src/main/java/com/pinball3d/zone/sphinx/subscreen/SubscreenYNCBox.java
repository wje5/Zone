package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenYNCBox extends Subscreen {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public String title, text;
	public Runnable yes, no;

	public SubscreenYNCBox(IHasSubscreen parent, String title, String text, Runnable yes, Runnable no) {
		this(parent, getDisplayWidth() / 2 - 75, getDisplayHeight() / 2 - 50, title, text, yes, no);
	}

	public SubscreenYNCBox(IHasSubscreen parent, int x, int y, String title, String text, Runnable yes, Runnable no) {
		super(parent, x, y, 150, 100, true);
		components.add(new TextButton(this, this.x + 15, this.y + 80, I18n.format("sphinx.yes"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
			if (yes != null) {
				yes.run();
			}
		}));
		components.add(new TextButton(this, this.x + 55, this.y + 80, I18n.format("sphinx.no"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
			if (no != null) {
				no.run();
			}
		}));
		components.add(new TextButton(this, this.x + 95, this.y + 80, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenYNCBox.this);
		}));
		this.title = title;
		this.text = text;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.25F);
		Util.drawTexture(TEXTURE, x + 130, y, 80, 0, 80, 80, 0.25F);
		Util.drawTexture(TEXTURE, x, y + 80, 0, 80, 80, 80, 0.25F);
		Util.drawTexture(TEXTURE, x + 130, y + 80, 80, 80, 80, 80, 0.25F);
		Gui.drawRect(x + 20, y, x + 130, y + 20, 0x2F000000);
		Gui.drawRect(x, y + 20, x + 150, y + 80, 0x2F000000);
		Gui.drawRect(x + 20, y + 80, x + 130, y + 100, 0x2F000000);
		Gui.drawRect(x + 5, y + 10, x + 145, y + 11, 0xFF20E6EF);
		Gui.drawRect(x + 8, y + 12, x + 142, y + 97, 0x651CC3B5);
		Util.getFontRenderer().drawString(title, x + 7, y + 2, 0xFF1ECCDE);
		Util.getFontRenderer().drawSplitString(text, x + 15, y + 15, 120, 0xFF1ECCDE);
		Util.drawBorder(x + 7, y + 12, 135, 86, 1, 0xFF1ECCDE);
	}
}