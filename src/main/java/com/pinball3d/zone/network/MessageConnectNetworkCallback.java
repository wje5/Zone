package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.SubscreenConnectToNetwork;
import com.pinball3d.zone.sphinx.SubscreenNetworkConfig;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectNetworkCallback implements IMessage {
	UUID uuid;
	WorldPos pos;
	String password;

	public MessageConnectNetworkCallback() {

	}

	public MessageConnectNetworkCallback(UUID uuid, WorldPos pos, String password) {
		this.uuid = uuid;
		this.pos = pos;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		pos = WorldPos.readFromByte(buf);
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		pos.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	public static class Handler implements IMessageHandler<MessageConnectNetworkCallback, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectNetworkCallback message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						ScreenTerminal terminal = (ScreenTerminal) screen;
						terminal.worldpos = message.pos;
						if (terminal.stack != ItemStack.EMPTY) {
							NBTTagCompound tag = terminal.stack.getTagCompound();
							if (tag == null) {
								tag = new NBTTagCompound();
								terminal.stack.setTagCompound(tag);
							}
							tag.setUniqueId("network", message.uuid);
							tag.setString("password", message.password);
						}
						if (!terminal.subscreens.isEmpty()) {
							Subscreen subscreen = terminal.subscreens.get(0);
							if (subscreen instanceof SubscreenNetworkConfig) {
								if (!subscreen.subscreens.empty()) {
									Subscreen s = subscreen.subscreens.get(0);
									if (s instanceof SubscreenConnectToNetwork) {
										((SubscreenConnectToNetwork) s).setData(true);
									}
								}
								((SubscreenNetworkConfig) subscreen).refresh();
							}
						}
					} else if (screen instanceof ScreenNeedNetwork) {
						INeedNetwork tileentity = ((ScreenNeedNetwork) screen).tileentity;
						WorldPos pos = tileentity.getNetworkPos();
						if (pos != null) {
							if (pos.getTileEntity() instanceof TEProcessingCenter) {
								((TEProcessingCenter) pos.getTileEntity()).removeNeedNetwork(message.pos);
							}
						} else if (tileentity.getNetwork() != null) {
							pos = GlobalNetworkData.getData(((TileEntity) tileentity).getWorld())
									.getNetwork(tileentity.getNetwork());
							if (pos.getTileEntity() instanceof TEProcessingCenter) {
								((TEProcessingCenter) pos.getTileEntity()).removeNeedNetwork(message.pos);
							}
						}
						tileentity.connect(message.uuid, message.password);
						tileentity.setWorldPos(message.pos, message.uuid);
						ScreenNeedNetwork s = (ScreenNeedNetwork) screen;
						if (!s.subscreens.isEmpty()) {
							Subscreen subscreen = s.subscreens.get(0);
							if (subscreen instanceof SubscreenNetworkConfig) {
								if (!subscreen.subscreens.empty()) {
									Subscreen sub = subscreen.subscreens.get(0);
									if (sub instanceof SubscreenConnectToNetwork) {
										((SubscreenConnectToNetwork) sub).setData(true);
									}
								}
								((SubscreenNetworkConfig) subscreen).refresh();
							}
						}
					}
				}
			});
			return null;
		}
	}
}
