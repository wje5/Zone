package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageDisconnect;
import com.pinball3d.zone.network.MessageTerminalDisconnect;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenCheckConnectedNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public WorldPos pos;
	public String name;

	public SubscreenCheckConnectedNetwork(IParent parent, WorldPos pos, String name) {
		this(parent, pos, name, parent.getWidth() / 2 - 82, parent.getHeight() / 2 - 17);
	}

	public SubscreenCheckConnectedNetwork(IParent parent, WorldPos pos, String name, int x, int y) {
		super(parent, x + 165 + parent.getXOffset() > displayWidth ? displayWidth - 165 - parent.getXOffset() : x,
				y + 35 + parent.getYOffset() > displayHeight ? displayHeight - 35 - parent.getYOffset() : y, 165, 35,
				false);
		this.pos = pos;
		this.name = name;
		components.add(new HyperTextButton(this, this.x + 35, this.y + 24, I18n.format("sphinx.info"), () -> {
			SubscreenNetworkConfig screen = (SubscreenNetworkConfig) parent;
			screen.quitScreen(SubscreenCheckConnectedNetwork.this);
			screen.parent.quitScreen(screen);
			screen.parent.putScreen(new SubscreenNetworkInfo(screen.parent, pos));
		}));
		components.add(new HyperTextButton(this, this.x + 70, this.y + 24, I18n.format("sphinx.disconnect"), () -> {
			if (((SubscreenNetworkConfig) parent).parent instanceof ScreenTerminal) {
				ScreenTerminal screen = (ScreenTerminal) ((SubscreenNetworkConfig) parent).parent;
				screen.resetNetwork();
				screen.worldpos = null;
				screen.flag = true;
				parent.quitScreen(SubscreenCheckConnectedNetwork.this);
				NetworkHandler.instance.sendToServer(new MessageTerminalDisconnect(mc.player));
			} else {
				NetworkHandler.instance.sendToServer(new MessageDisconnect(mc.player,
						((ScreenNeedNetwork) ((SubscreenNetworkConfig) parent).parent).pos));
				parent.quitScreen(SubscreenCheckConnectedNetwork.this);
			}
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 0, 16, 32, 25, 0.5F);
		parent.getFontRenderer().drawString(name, x + 35, y + 4, 0xFF1ECCDE);
	}
}
