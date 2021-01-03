package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageChangeAdminPassword;
import com.pinball3d.zone.network.MessageChangePassword;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenChangePassword extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private TextInputBox box1, box2;
	private boolean isChangeAdminPassword;

	public SubscreenChangePassword(IHasSubscreen parent, boolean flag) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, flag);
	}

	public SubscreenChangePassword(IHasSubscreen parent, int x, int y, boolean flag) {
		super(parent, x, y, 300, 200, true);
		isChangeAdminPassword = flag;
		components.add(box1 = new TextInputBox(this, x + 30, y + 50, 100, 8, () -> {
			box1.isFocus = true;
			box2.isFocus = false;
		}));
		components.add(box2 = new TextInputBox(this, x + 30, y + 90, 100, 8, () -> {
			box1.isFocus = false;
			box2.isFocus = true;
		}));
		components.add(new TextButton(this, this.x + 190, this.y + 175, I18n.format("sphinx.confirm"), () -> {
			if (box1.text.length() == 8 && box1.text.equals(box2.text)) {
				WorldPos p = ConnectHelperClient.getInstance().getNetworkPos();
				if (isChangeAdminPassword) {
					NetworkHandler.instance
							.sendToServer(MessageChangeAdminPassword.newMessage(Util.PASSWORD, p, box1.text));
				} else {
					NetworkHandler.instance.sendToServer(MessageChangePassword.newMessage(Util.PASSWORD, p, box1.text));
				}
				parent.removeScreen(SubscreenChangePassword.this);
				parent.putScreen(new SubscreenSphinxConfig(parent));
			}
		}));
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenChangePassword.this);
			parent.putScreen(new SubscreenSphinxConfig(parent));
		}));
	}

	@Override
	public boolean onQuit() {
		parent.removeScreen(this);
		parent.putScreen(new SubscreenSphinxConfig(parent));
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
			Util.getFontRenderer().drawString(I18n.format("sphinx.change_admin_password"), x + 15, y + 8, 0xFF1ECCDE);
			Util.getFontRenderer().drawString(I18n.format("sphinx.set_admin_password"), x + 30, y + 35, 0xFF1ECCDE);
			Util.getFontRenderer().drawString(I18n.format("sphinx.confirm_admin_password"), x + 30, y + 75, 0xFF1ECCDE);
		} else {
			Util.getFontRenderer().drawString(I18n.format("sphinx.change_login_password"), x + 15, y + 8, 0xFF1ECCDE);
			Util.getFontRenderer().drawString(I18n.format("sphinx.set_login_password"), x + 30, y + 35, 0xFF1ECCDE);
			Util.getFontRenderer().drawString(I18n.format("sphinx.confirm_login_password"), x + 30, y + 75, 0xFF1ECCDE);
		}
		if (box1.text.length() > 0 && box1.text.length() != 8) {
			Util.getFontRenderer().drawString(I18n.format("sphinx.password_length_error"), x + 30, y + 115, 0xFFDA2D2D);
		} else if (box1.text.length() > 0 && box2.text.length() > 0 && !box1.text.equals(box2.text)) {
			Util.getFontRenderer().drawString(I18n.format("sphinx.confirm_password_error"), x + 30, y + 115,
					0xFFDA2D2D);
		}
	}
}
