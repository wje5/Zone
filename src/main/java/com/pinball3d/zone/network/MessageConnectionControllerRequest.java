package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.actors.threadpool.Arrays;

public class MessageConnectionControllerRequest implements IMessage {
	UUID uuid;
	WorldPos canter;
	List<Type> types;

	public MessageConnectionControllerRequest() {

	}

	public MessageConnectionControllerRequest(EntityPlayer player, WorldPos canter, Type... types) {
		uuid = player.getUniqueID();
		this.canter = canter;
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		canter = WorldPos.readFromByte(buf);
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
		canter.writeToByte(buf);
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionControllerRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionControllerRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				UUID uuid = ((TEProcessingCenter) message.canter.getTileEntity()).getUUID();
				ConnectionHelper.refreshRequest(message.uuid, uuid, WorldPos.ORIGIN,
						message.types.toArray(new Type[] {}));
			});
			return null;
		}
	}
}
