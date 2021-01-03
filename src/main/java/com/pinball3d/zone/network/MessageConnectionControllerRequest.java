package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectionControllerRequest implements IMessage {
	UUID uuid;
	WorldPos controller;
	List<Type> types;

	public MessageConnectionControllerRequest() {

	}

	public MessageConnectionControllerRequest(EntityPlayer player, WorldPos controller, Type... types) {
		uuid = player.getUniqueID();
		this.controller = controller;
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		controller = WorldPos.readFromByte(buf);
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
		controller.writeToByte(buf);
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionControllerRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionControllerRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectionHelper.requestControllerConnect(message.uuid, message.controller,
						message.types.toArray(new Type[] {}));
			});
			return null;
		}
	}
}
