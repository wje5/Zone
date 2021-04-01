package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageChangeName;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenChangeName extends Subscreen {
	private TextInputBox box;
	private boolean hasData;

	public SubscreenChangeName(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenChangeName(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(box = new TextInputBox(this, 30, 50, 100, 12, () -> {
			box.isFocus = true;
		}));
		addComponent(new TextButton(this, 190, 175, I18n.format("sphinx.confirm"), () -> {
			if (hasData && box.text.length() >= 4) {
				NetworkHandler.instance.sendToServer(
						MessageChangeName.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), box.text));
				parent.removeScreen(SubscreenChangeName.this);
				parent.putScreen(new SubscreenSphinxConfig(parent));
			}
		}));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenChangeName.this);
			parent.putScreen(new SubscreenSphinxConfig(parent));
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.NAME);
		return s;
	}

	@Override
	public void update() {
		super.update();
		if (!hasData && ConnectHelperClient.getInstance().hasData()) {
			box.text = ConnectHelperClient.getInstance().getName();
			hasData = true;
		}
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
		Util.renderGlowString(I18n.format("sphinx.change_sphinx_name"), 15, 8);
		Util.renderGlowString(I18n.format("sphinx.set_sphinx_name"), 30, 35);
		if (box.text.length() > 0 && box.text.length() < 4) {
			Util.renderGlowString(I18n.format("sphinx.name_length_error"), 30, 155, 0xFFFC3D3D, 0xFFEF2020);
		}
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
