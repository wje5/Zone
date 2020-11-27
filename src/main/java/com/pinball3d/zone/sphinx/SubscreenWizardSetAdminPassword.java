package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenWizardSetAdminPassword extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private TextInputBox box1, box2;

	public SubscreenWizardSetAdminPassword(IParent parent) {
		this(parent, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100);
	}

	public SubscreenWizardSetAdminPassword(IParent parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		components.add(box1 = new TextInputBox(this, x + 30, y + 50, 100, 8, () -> {
			box1.isFocus = true;
			box2.isFocus = false;
		}));
		components.add(box2 = new TextInputBox(this, x + 30, y + 90, 100, 8, () -> {
			box1.isFocus = false;
			box2.isFocus = true;
		}));
		String s = I18n.format("sphinx.cancel");
		int w = getFontRenderer().getStringWidth(s);
		components.add(new TextButton(this, this.x + 262 - w, this.y + 175, s, () -> {
			subscreens.push(new SubscreenQuitWizard(SubscreenWizardSetAdminPassword.this));
		}));
		s = I18n.format("sphinx.next");
		w += getFontRenderer().getStringWidth(s);
		components.add(new TextButton(this, this.x + 242 - w, this.y + 175, s, () -> {
			if (box1.text.length() == 8 && box1.text.equals(box2.text)) {
				parent.quitScreen(SubscreenWizardSetAdminPassword.this);
				parent.putScreen(new SubscreenWizardSetLoginPassword(parent, box1.text));
			}
		}));
		s = I18n.format("sphinx.back");
		w += getFontRenderer().getStringWidth(s);
		components.add(new TextButton(this, this.x + 222 - w, this.y + 175, I18n.format("sphinx.back"), () -> {
			parent.quitScreen(SubscreenWizardSetAdminPassword.this);
			parent.putScreen(new SubscreenSphinxInitWizard(parent));
		}));
	}

	@Override
	public boolean onQuit() {
		if (subscreens.empty()) {
			subscreens.push(new SubscreenQuitWizard(this));
			return false;
		}
		if (subscreens.peek().onQuit()) {
			subscreens.pop();
		}
		return false;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 260, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 300, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 260, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 290, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		parent.getFontRenderer().drawString(I18n.format("sphinx.sphinx_init_wizard"), x + 15, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.set_admin_password"), x + 30, y + 35, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.confirm_admin_password"), x + 30, y + 75, 0xFF1ECCDE);
		if (box1.text.length() > 0 && box1.text.length() != 8) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.password_length_error"), x + 30, y + 115,
					0xFFDA2D2D);
		} else if (box1.text.length() > 0 && box2.text.length() > 0 && !box1.text.equals(box2.text)) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.confirm_password_error"), x + 30, y + 115,
					0xFFDA2D2D);
		}
	}
}
