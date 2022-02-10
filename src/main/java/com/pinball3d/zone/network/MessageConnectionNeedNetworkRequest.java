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
	UUID playerUUID, networkUUID;
	WorldPos terminal;
	List<Type> types;

	public MessageConnectionNeedNetworkRequest() {

	}

	public MessageConnectionNeedNetworkRequest(EntityPlayer player, WorldPos terminal, UUID network, Type... types) {
		playerUUID = player.getUniqueID();
		this.terminal = terminal;
		networkUUID = network;
		this.types = Arrays.asList(types);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		playerUUID = new UUID(buf.readLong(), buf.readLong());
		terminal = WorldPos.readFromByte(buf);
		networkUUID = new UUID(buf.readLong(), buf.readLong());
		types = new ArrayList<Type>();
		for (Type i : Type.values()) {
			if (buf.readBoolean()) {
				types.add(i);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(playerUUID.getMostSignificantBits());
		buf.writeLong(playerUUID.getLeastSignificantBits());
		terminal.writeToByte(buf);
		buf.writeLong(networkUUID.getMostSignificantBits());
		buf.writeLong(networkUUID.getLeastSignificantBits());
		for (Type i : Type.values()) {
			buf.writeBoolean(types.contains(i));
		}
	}

	public static class Handler implements IMessageHandler<MessageConnectionNeedNetworkRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectionNeedNetworkRequest message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
						.getPlayerByUUID(message.playerUUID);
				ConnectionHelper.requestTerminalConnect(player, message.terminal, message.networkUUID,
						message.types.toArray(new Type[] {}));
			});
			return null;
		}
	}
}
