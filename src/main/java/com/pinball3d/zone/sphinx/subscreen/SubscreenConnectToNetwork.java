package com.pinball3d.zone.sphinx.subscreen;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.MessageTryConnectToNetwork;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenConnectToNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private String input = "";
	private WorldPos pos, network;
	private String name;
	private long quit;
	private boolean waiting;

	public SubscreenConnectToNetwork(IHasSubscreen parent, WorldPos pos, WorldPos network, String name) {
		this(parent, pos, network, name, getDisplayWidth() / 2 - 82, getDisplayHeight() / 2 - 17);
	}

	public SubscreenConnectToNetwork(IHasSubscreen parent, WorldPos pos, WorldPos network, String name, int x, int y) {
		super(parent, x, y, 165, 35, false);
		this.pos = pos;
		this.network = network;
		this.name = name;
	}

	public void setData(boolean flag) {
		if (flag) {
			dead = true;
			quit = 0;
		} else {
			waiting = false;
			quit = mc.world.getTotalWorldTime();
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 48, 0, 36, 36, 0.5F);
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(I18n.format("sphinx.connect_to_network", name), x + 35, y + 4, 0xFF1ECCDE);
		if (waiting) {
			fr.drawString(I18n.format("sphinx.waiting_for_server"), x + 35, y + 20, 0xFF1ECCDE);
		} else if (quit > 0) {
			fr.drawString(I18n.format("sphinx.password_incorrect"), x + 35, y + 20, 0xFF1ECCDE);
		} else {
			for (int i = 0; i < input.length(); i++) {
				Util.drawTexture(TEXTURE, x + 35 + i * 16, y + 18, 0, 118, 21, 21, 0.5F);
			}
		}
		if (quit > 0 && mc.world.getTotalWorldTime() - quit > 20) {
			dead = true;
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (quit > 0) {
			return;
		}
		if (keyCode == Keyboard.KEY_BACK && input.length() >= 1) {
			input = input.substring(0, input.length() - 1);
		}
		if (Util.isValidChar(typedChar, 7)) {
			input += typedChar;
			if (input.length() == 8) {
				NetworkHandler.instance.sendToServer(new MessageTryConnectToNetwork(mc.player, pos.isOrigin(),
						pos.isOrigin() ? new WorldPos(mc.player.getPosition(), mc.world.provider.getDimension()) : pos,
						network, input));
				waiting = true;
				quit = mc.world.getTotalWorldTime();
			}
		}
	}
}
