package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageDisconnect;
import com.pinball3d.zone.network.MessageTerminalDisconnect;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class SubscreenCheckConnectedNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public TEProcessingCenter tileentity;

	public SubscreenCheckConnectedNetwork(IParent parent, TEProcessingCenter tileentity) {
		this(parent, tileentity, parent.getWidth() / 2 - 82, parent.getHeight() / 2 - 17);
	}

	public SubscreenCheckConnectedNetwork(IParent parent, TEProcessingCenter tileentity, int x, int y) {
		super(parent, x + 165 + parent.getXOffset() > displayWidth ? displayWidth - 165 - parent.getXOffset() : x,
				y + 35 + parent.getYOffset() > displayHeight ? displayHeight - 35 - parent.getYOffset() : y, 165, 35,
				false);
		this.tileentity = tileentity;
		components.add(new HyperTextHutton(this, this.x + 35, this.y + 24, I18n.format("sphinx.info"), new Runnable() {
			@Override
			public void run() {
				SubscreenNetworkConfig screen = (SubscreenNetworkConfig) parent;
				screen.quitScreen(SubscreenCheckConnectedNetwork.this);
				screen.parent.quitScreen(screen);
				screen.parent.putScreen(new SubscreenNetworkInfo(parent, tileentity));

			}
		}));
		components.add(
				new HyperTextHutton(this, this.x + 70, this.y + 24, I18n.format("sphinx.disconnect"), new Runnable() {
					@Override
					public void run() {
						if (((SubscreenNetworkConfig) parent).parent instanceof ScreenTerminal) {
							ScreenTerminal screen = (ScreenTerminal) ((SubscreenNetworkConfig) parent).parent;
							screen.resetNetwork();
							screen.worldpos = null;
							parent.quitScreen(SubscreenCheckConnectedNetwork.this);
							NetworkHandler.instance.sendToServer(new MessageTerminalDisconnect(mc.player));
							((SubscreenNetworkConfig) parent).refresh();
						} else {
							INeedNetwork te = ((ScreenNeedNetwork) ((SubscreenNetworkConfig) parent).parent).tileentity;
							WorldPos pos = te.getNetworkPos();
							if (pos != null) {
								TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
								WorldPos pos2 = new WorldPos((TileEntity) te);
								NetworkHandler.instance.sendToServer(new MessageDisconnect(pos2));
								pc.removeNeedNetwork(pos2);
							} else if (te.getNetwork() != null) {
								pos = GlobalNetworkData.getData(((TileEntity) te).getWorld())
										.getNetwork(te.getNetwork());
								WorldPos pos2 = new WorldPos((TileEntity) te);
								NetworkHandler.instance.sendToServer(new MessageDisconnect(pos2));
								TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
								pc.removeNeedNetwork(pos2);
							}
							te.deleteNetwork();
							parent.quitScreen(SubscreenCheckConnectedNetwork.this);
							((SubscreenNetworkConfig) parent).refresh();
						}
					}
				}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 0, 16, 32, 25, 0.5F);
		parent.getFontRenderer().drawString(tileentity.getName(), x + 35, y + 4, 0xFF1ECCDE);
	}
}