package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.SubscreenNetworkConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendValidNetworkData implements IMessage {
	NBTTagCompound tag;

	public MessageSendValidNetworkData() {

	}

	public MessageSendValidNetworkData(NBTTagCompound tag) {
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<MessageSendValidNetworkData, IMessage> {
		@Override
		public IMessage onMessage(MessageSendValidNetworkData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						ScreenTerminal terminal = (ScreenTerminal) screen;
						if (!terminal.subscreens.empty()
								&& terminal.subscreens.get(0) instanceof SubscreenNetworkConfig) {
							((SubscreenNetworkConfig) terminal.subscreens.get(0)).list.setData(message.tag);
						}
					}
					if (screen instanceof ScreenNeedNetwork) {
						ScreenNeedNetwork s = (ScreenNeedNetwork) screen;
						if (!s.subscreens.empty() && s.subscreens.get(0) instanceof SubscreenNetworkConfig) {
							((SubscreenNetworkConfig) s.subscreens.get(0)).list.setData(message.tag);
						}
					}
				}
			});
			return null;
		}
	}
}