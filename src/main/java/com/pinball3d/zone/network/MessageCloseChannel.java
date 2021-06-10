package com.pinball3d.zone.network;

import com.pinball3d.zone.instrument.ChannelManager;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCloseChannel implements IMessage {
	public MessageCloseChannel() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageCloseChannel, IMessage> {
		@Override
		public IMessage onMessage(MessageCloseChannel message, MessageContext ctx) {
			ChannelManager.getInstance().closeChannel(ctx.getServerHandler().player);
			return null;
		}
	}
}
