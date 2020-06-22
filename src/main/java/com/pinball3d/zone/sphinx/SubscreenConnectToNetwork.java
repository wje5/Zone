package com.pinball3d.zone.sphinx;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.network.MessageConnectToNetwork;
import com.pinball3d.zone.network.MessageTerminalConnectToNetwork;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TENode;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class SubscreenConnectToNetwork extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private String input = "";
	public TEProcessingCenter tileentity;
	private long quit;

	public SubscreenConnectToNetwork(IParent parent, TEProcessingCenter tileentity) {
		this(parent, tileentity, parent.getWidth() / 2 - 82, parent.getHeight() / 2 - 17);
	}

	public SubscreenConnectToNetwork(IParent parent, TEProcessingCenter tileentity, int x, int y) {
		super(parent, x + 165 + parent.getXOffset() > displayWidth ? displayWidth - 165 - parent.getXOffset() : x,
				y + 35 + parent.getYOffset() > displayHeight ? displayHeight - 35 - parent.getYOffset() : y, 165, 35,
				false);
		this.tileentity = tileentity;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawTexture(TEXTURE, x + 8, y + 8, 48, 0, 36, 36, 0.5F);
		parent.getFontRenderer().drawString(I18n.format("sphinx.connect_to_network", tileentity.getName()), x + 35,
				y + 4, 0xFF1ECCDE);
		if (quit > 0) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.password_incorrect"), x + 35, y + 20, 0xFF1ECCDE);
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
		if (!subscreens.empty()) {
			subscreens.peek().keyTyped(typedChar, keyCode);
		} else {
			if (keyCode == Keyboard.KEY_BACK && input.length() >= 1) {
				input = input.substring(0, input.length() - 1);
			}
			if (Util.isValidChar(typedChar, 7)) {
				input += typedChar;
				if (input.length() >= 8) {
					if (tileentity.isCorrectLoginPassword(input)) {
						parent.quitScreen(this);
						ItemStack stack = parent.getTerminal();
						if (stack != ItemStack.EMPTY) {
							WorldPos pos = new WorldPos(tileentity.getPos(), tileentity.getWorld());
							NetworkHandler.instance
									.sendToServer(new MessageTerminalConnectToNetwork(pos, mc.player.getName()));
							NBTTagCompound tag = stack.getTagCompound();
							if (tag == null) {
								tag = new NBTTagCompound();
							}
							tag.setUniqueId("network", tileentity.getUUID());
							stack.setTagCompound(tag);
						} else {
							INeedNetwork te = parent.getNeedNetworkTileEntity();
							WorldPos pos1 = new WorldPos(tileentity.getPos(), tileentity.getWorld());
							WorldPos pos2 = new WorldPos(((TileEntity) te).getPos(), ((TileEntity) te).getWorld());
							NetworkHandler.instance.sendToServer(new MessageConnectToNetwork(pos1, pos2));
							te.connect(tileentity.getUUID());
							tileentity.addNode(pos2);
						}
						if (((SubscreenNetworkConfig) parent).parent instanceof ScreenTerminal) {
							((ScreenTerminal) ((SubscreenNetworkConfig) parent).parent).worldpos = new WorldPos(
									tileentity.getPos(), tileentity.getWorld());
						} else {
							TENode tenode = ((ScreenNode) ((SubscreenNetworkConfig) parent).parent).tileentity;
							tenode.setWorldPos(new WorldPos(tileentity.getPos(), tileentity.getWorld()),
									tenode.getNetwork());
						}
						((SubscreenNetworkConfig) parent).refresh();
					} else {
						quit = mc.world.getTotalWorldTime();
					}
				}
			}
		}
	}
}
