package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenSphinxAdvenced;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.SubscreenNeedNetworkInfo;
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

public class MessageSendNeedNetworkInfoToClient implements IMessage {
	WorldPos pos;
	NBTTagCompound data;

	public MessageSendNeedNetworkInfoToClient() {

	}

	public MessageSendNeedNetworkInfoToClient(WorldPos pos, NBTTagCompound data) {
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

	public static class Handler implements IMessageHandler<MessageSendNeedNetworkInfoToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendNeedNetworkInfoToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof ScreenSphinxAdvenced) {
					ScreenSphinxAdvenced adv = (ScreenSphinxAdvenced) screen;
					if (!adv.subscreens.empty()) {
						Subscreen s = adv.subscreens.get(0);
						if (s instanceof SubscreenNeedNetworkInfo) {
							((SubscreenNeedNetworkInfo) s).setData(message.pos, message.data);
						}
					}
				}
			});
			return null;
		}
	}
}
