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

public class MessageConnectionNeedNetworkRequest implements IMessage {
	UUID uuid;
	WorldPos needNetwork;
	List<Type> types;

	public MessageConnectionNeedNetworkRequest() {

	}

	public MessageConnectionNeedNetworkRequest(EntityPlayer player, WorldPos needNetwork, Type... types) {
		uuid = player.getUniqueID();
		this.needNetwork = needNetwork;
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		needNetwork = WorldPos.readFromByte(buf);
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
		needNetwork.writeToByte(buf);
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionNeedNetworkRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionNeedNetworkRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
//				UUID network = ((INeedNetwork) message.needNetwork.getTileEntity()).getNetwork();
//				ConnectionHelper.refreshRequest(message.uuid, network, message.needNetwork,
//						message.types.toArray(new Type[] {}));TODO
			});
			return null;
		}
	}
}
