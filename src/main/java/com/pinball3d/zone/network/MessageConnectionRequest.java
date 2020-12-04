package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.actors.threadpool.Arrays;

public class MessageConnectionRequest implements IMessage {
	UUID uuid, network;
	WorldPos needNetwork;
	List<Type> types;

	public MessageConnectionRequest() {

	}

	public MessageConnectionRequest(EntityPlayer player, UUID network, WorldPos needNetwork, Type... types) {
		uuid = player.getUniqueID();
		this.network = network;
		this.needNetwork = needNetwork;
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		long l = buf.readLong();
		long l2 = buf.readLong();
		if (l == 0 && l2 == 0) {
			network = null;
		} else {
			network = new UUID(l, l2);
		}
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
		if (network != null) {
			buf.writeLong(network.getMostSignificantBits());
			buf.writeLong(network.getLeastSignificantBits());
		} else {
			buf.writeLong(0);
			buf.writeLong(0);
		}
		needNetwork.writeToByte(buf);
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ConnectionHelper.refreshRequest(message.uuid, message.network, message.needNetwork,
						message.types.toArray(new Type[] {}));
			});
			return null;
		}
	}
}
