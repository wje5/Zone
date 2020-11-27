package com.pinball3d.zone.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectionUpdate implements IMessage {
	UUID network;
	NBTTagCompound tag;

	public MessageConnectionUpdate() {

	}

	public MessageConnectionUpdate(UUID network, NBTTagCompound tag) {
		this.network = network;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		network = new UUID(buf.readLong(), buf.readLong());
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(network.getMostSignificantBits());
		buf.writeLong(network.getLeastSignificantBits());
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<MessageConnectionUpdate, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionUpdate message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectHelperClient.instance.setData(message.network, message.tag);
			});
			return null;
		}
	}
}
