package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.SubscreenNetworkInfo;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendNetworkInfoToClient implements IMessage {
	WorldPos pos;
	NBTTagCompound data;

	public MessageSendNetworkInfoToClient() {

	}

	public MessageSendNetworkInfoToClient(WorldPos pos, NBTTagCompound data) {
		this.pos = pos;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		ByteBufUtils.writeTag(buf, data);
	}

	public static class Handler implements IMessageHandler<MessageSendNetworkInfoToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendNetworkInfoToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						ScreenTerminal terminal = (ScreenTerminal) screen;
						if (!terminal.subscreens.empty()) {
							Subscreen s = terminal.subscreens.get(0);
							if (s instanceof SubscreenNetworkInfo) {
								((SubscreenNetworkInfo) s).setData(message.pos, message.data);
							}
						}
					} else if (screen instanceof ScreenNeedNetwork) {
						ScreenNeedNetwork n = (ScreenNeedNetwork) screen;
						if (!n.subscreens.empty()) {
							Subscreen s = n.subscreens.get(0);
							if (s instanceof SubscreenNetworkInfo) {
								((SubscreenNetworkInfo) s).setData(message.pos, message.data);
							}
						}
					}
				}
			});
			return null;
		}
	}
}