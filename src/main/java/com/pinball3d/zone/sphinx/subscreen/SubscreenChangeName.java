package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageChangeName;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenChangeName extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private TextInputBox box;
	private boolean hasData;

	public SubscreenChangeName(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenChangeName(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		components.add(box = new TextInputBox(this, x + 30, y + 50, 100, 12, () -> {
			box.isFocus = true;
		}));
		components.add(new TextButton(this, this.x + 190, this.y + 175, I18n.format("sphinx.confirm"), () -> {
			if (hasData && box.text.length() >= 4) {
				NetworkHandler.instance.sendToServer(MessageChangeName.newMessage(mc.player,
						ConnectHelperClient.getInstance().getNetworkPos(), box.text));
				parent.removeScreen(SubscreenChangeName.this);
				parent.putScreen(new SubscreenSphinxConfig(parent));
			}
		}));
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.cancel"), () -> {
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
		Util.getFontRenderer().drawString(I18n.format("sphinx.change_sphinx_name"), x + 15, y + 8, 0xFF1ECCDE);
		Util.getFontRenderer().drawString(I18n.format("sphinx.set_sphinx_name"), x + 30, y + 35, 0xFF1ECCDE);
		if (box.text.length() > 0 && box.text.length() < 4) {
			Util.getFontRenderer().drawString(I18n.format("sphinx.name_length_error"), x + 30, y + 155, 0xFFDA2D2D);
		}
	}
}
