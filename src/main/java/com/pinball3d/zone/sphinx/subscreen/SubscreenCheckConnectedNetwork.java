package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.MessageDisconnect;
import com.pinball3d.zone.network.MessageTerminalDisconnect;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.GuiContainerNeedNetwork;
import com.pinball3d.zone.sphinx.GuiContainerTerminal;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.HyperTextButton;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenCheckConnectedNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public WorldPos pos;
	public String name;

	public SubscreenCheckConnectedNetwork(IHasSubscreen parent, WorldPos pos, String name) {
		this(parent, pos, name, getDisplayWidth() / 2 - 82, getDisplayHeight() / 2 - 17);
	}

	public SubscreenCheckConnectedNetwork(IHasSubscreen parent, WorldPos pos, String name, int x, int y) {
		super(parent, x, y, 165, 35, false);
		this.pos = pos;
		this.name = name;
		components.add(new HyperTextButton(this, this.x + 35, this.y + 24, I18n.format("sphinx.info"), () -> {
			Iterator<Subscreen> it = parent.getSubscreens().iterator();
			while (it.hasNext()) {
				Subscreen s = it.next();
				if (s instanceof SubscreenNetworkConfig) {
					it.remove();
				}
			}
			parent.removeScreen(SubscreenCheckConnectedNetwork.this);
			parent.putScreen(new SubscreenNetworkInfo(parent, pos));
		}));
		components.add(new HyperTextButton(this, this.x + 70, this.y + 24, I18n.format("sphinx.disconnect"), () -> {
			if (parent instanceof GuiContainerTerminal) {
				parent.removeScreen(SubscreenCheckConnectedNetwork.this);
				NetworkHandler.instance.sendToServer(new MessageTerminalDisconnect(mc.player));
			} else if (parent instanceof GuiContainerNeedNetwork) {
				NetworkHandler.instance
						.sendToServer(new MessageDisconnect(mc.player, ((GuiContainerNeedNetwork) parent).pos));
				parent.removeScreen(SubscreenCheckConnectedNetwork.this);
			}
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 0, 16, 32, 25, 0.5F);
		Util.renderGlowString(name, x + 35, y + 4);
	}
}
