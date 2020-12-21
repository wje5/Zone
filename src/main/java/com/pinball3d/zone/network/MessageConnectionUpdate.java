package com.pinball3d.zone.network;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.pinball3d.zone.network.ConnectionHelper.Type;

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
	Set<Type> types;

	public MessageConnectionUpdate() {

	}

	public MessageConnectionUpdate(UUID network, NBTTagCompound tag, Type... types) {
		this.network = network;
		this.tag = tag;
		this.types = Sets.newHashSet(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
//		System.out.println(buf.readableBytes());
		long l = buf.readLong();
		long l2 = buf.readLong();
		if (l == 0 && l2 == 0) {
			network = null;
		} else {
			network = new UUID(l, l2);
		}
		tag = ByteBufUtils.readTag(buf);
		types = new HashSet<Type>();
		for (Type i : Type.values()) {
			if (buf.readBoolean()) {
				types.add(i);
			}
		}
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
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionUpdate, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionUpdate message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectHelperClient.getInstance().setData(message.network, message.tag, message.types);
			});
			return null;
		}
	}
}
