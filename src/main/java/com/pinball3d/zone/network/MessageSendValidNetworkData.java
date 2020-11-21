package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenSphinxBase;
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
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof ScreenTerminal || screen instanceof ScreenNeedNetwork) {
					ScreenSphinxBase base = (ScreenSphinxBase) screen;
					if (!base.subscreens.empty() && base.subscreens.get(0) instanceof SubscreenNetworkConfig) {
						((SubscreenNetworkConfig) base.subscreens.get(0)).list.setData(message.tag);
					}
				}
			});
			return null;
		}
	}
}