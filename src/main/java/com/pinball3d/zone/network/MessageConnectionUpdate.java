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
		long l = buf.readLong();
		long l2 = buf.readLong();
		if (l == 0 && l2 == 0) {
			network = null;
		} else {
			network = new UUID(l, l2);
		}
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (network != null) {
			buf.writeLong(network.getMostSignificantBits());
			buf.writeLong(network.getLeastSignificantBits());
		} else {
			buf.writeLong(0);
			buf.writeLong(0);
		}
		ByteBufUtils.writeTag(buf, tag);
		System.out.println(buf.readableBytes());
	}

	public static class Handler implements IMessageHandler<MessageConnectionUpdate, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionUpdate message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectHelperClient.getInstance().setData(message.network, message.tag);
			});
			return null;
		}
	}
}
