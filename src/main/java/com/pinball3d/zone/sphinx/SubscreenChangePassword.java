package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageChangeAdminPassword;
import com.pinball3d.zone.network.MessageChangePassword;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenChangePassword extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private TextInputBox box1, box2;
	private boolean isChangeAdminPassword;

	public SubscreenChangePassword(IParent parent, boolean flag) {
		this(parent, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100, flag);
	}

	public SubscreenChangePassword(IParent parent, int x, int y, boolean flag) {
		super(parent, x, y, 300, 200, true);
		isChangeAdminPassword = flag;
		components.add(box1 = new TextInputBox(this, x + 30, y + 50, 100, 8, new Runnable() {
			@Override
			public void run() {
				box1.isFocus = true;
				box2.isFocus = false;
			}
		}));
		components.add(box2 = new TextInputBox(this, x + 30, y + 90, 100, 8, new Runnable() {
			@Override
			public void run() {
				box1.isFocus = false;
				box2.isFocus = true;
			}
		}));
		components.add(new TextButton(this, this.x + 190, this.y + 175, I18n.format("sphinx.confirm"), new Runnable() {
			@Override
			public void run() {
				if (box1.text.length() == 8 && box1.text.equals(box2.text)) {
					TEProcessingCenter te = ((ScreenSphinxController) parent).tileentity;
					if (isChangeAdminPassword) {
						NetworkHandler.instance.sendToServer(
								MessageChangeAdminPassword.newMessage(((ScreenSphinxController) parent).password,
										new WorldPos(te.getPos(), te.getWorld()), box1.text));
						te.setAdminPassword(box1.text);
						parent.quitScreen(SubscreenChangePassword.this);
						parent.putScreen(new SubscreenSphinxConfig(parent));
					} else {
						NetworkHandler.instance.sendToServer(
								MessageChangePassword.newMessage(((ScreenSphinxController) parent).password,
										new WorldPos(te.getPos(), te.getWorld()), box1.text));
						te.setPassword(box1.text);
						parent.quitScreen(SubscreenChangePassword.this);
						parent.putScreen(new SubscreenSphinxConfig(parent));
					}
				}
			}
		}));
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.cancel"), new Runnable() {
			@Override
			public void run() {
				parent.quitScreen(SubscreenChangePassword.this);
				parent.putScreen(new SubscreenSphinxConfig(parent));
			}
		}));
	}

	@Override
	public boolean onQuit() {
		if (subscreens.empty()) {
			parent.quitScreen(this);
			parent.putScreen(new SubscreenSphinxConfig(parent));
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
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
		if (isChangeAdminPassword) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.change_admin_password"), x + 15, y + 8, 0xFF1ECCDE);
			parent.getFontRenderer().drawString(I18n.format("sphinx.set_admin_password"), x + 30, y + 35, 0xFF1ECCDE);
			parent.getFontRenderer().drawString(I18n.format("sphinx.confirm_admin_password"), x + 30, y + 75,
					0xFF1ECCDE);
		} else {
			parent.getFontRenderer().drawString(I18n.format("sphinx.change_login_password"), x + 15, y + 8, 0xFF1ECCDE);
			parent.getFontRenderer().drawString(I18n.format("sphinx.set_login_password"), x + 30, y + 35, 0xFF1ECCDE);
			parent.getFontRenderer().drawString(I18n.format("sphinx.confirm_login_password"), x + 30, y + 75,
					0xFF1ECCDE);
		}
		if (box1.text.length() > 0 && box1.text.length() != 8) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.password_length_error"), x + 30, y + 115,
					0xFFDA2D2D);
		} else if (box1.text.length() > 0 && box2.text.length() > 0 && !box1.text.equals(box2.text)) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.confirm_password_error"), x + 30, y + 115,
					0xFFDA2D2D);
		}
	}
}
