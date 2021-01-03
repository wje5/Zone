package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectionRequest implements IMessage {
	UUID uuid;
	List<Type> types;

	public MessageConnectionRequest() {

	}

	public MessageConnectionRequest(EntityPlayer player, Type... types) {
		uuid = player.getUniqueID();
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		types = new ArrayList<Type>();
		for (Type i : Type.values()) {
			if (buf.readBoolean()) {
				types.add(i);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectionHelper.requestTerminalConnect(message.uuid, message.types.toArray(new Type[] {}));
			});
			return null;
		}
	}
}
