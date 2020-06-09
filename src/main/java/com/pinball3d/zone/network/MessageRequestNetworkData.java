package com.pinball3d.zone.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNetworkData implements IMessage {
	public MessageRequestNetworkData() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageRequestNetworkData, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestNetworkData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
//					GlobalNetworkData.getData(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0))
//							.sendDataToClient();
//					TODO
				}
			});
			return null;
		}
	}
}
