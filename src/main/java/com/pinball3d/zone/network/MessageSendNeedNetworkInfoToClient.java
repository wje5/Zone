package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNeedNetworkInfo;

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
	SerialNumber serial;
	NBTTagCompound data;

	public MessageSendNeedNetworkInfoToClient() {

	}

	public MessageSendNeedNetworkInfoToClient(SerialNumber serial, NBTTagCompound data) {
		this.serial = serial;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		serial = new SerialNumber(buf);
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		serial.writeToByte(buf);
		ByteBufUtils.writeTag(buf, data);
	}

	public static class Handler implements IMessageHandler<MessageSendNeedNetworkInfoToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendNeedNetworkInfoToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerSphinxAdvanced) {
					GuiContainerSphinxAdvanced adv = (GuiContainerSphinxAdvanced) screen;
					if (!adv.getSubscreens().empty()) {
						adv.getSubscreens().forEach(s -> {
							if (s instanceof SubscreenNeedNetworkInfo) {
								((SubscreenNeedNetworkInfo) s).setData(message.serial, message.data);
							}
						});
					}
				}
			});
			return null;
		}
	}
}
