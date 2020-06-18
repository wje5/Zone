package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendUUIDToClient implements IMessage {
	WorldPos worldpos;
	UUID uuid;

	public MessageSendUUIDToClient() {

	}

	public MessageSendUUIDToClient(WorldPos worldpos, UUID uuid) {
		this.worldpos = worldpos;
		this.uuid = uuid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		worldpos = WorldPos.readFromByte(buf);
		uuid = new UUID(buf.readLong(), buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		worldpos.writeToByte(buf);
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}

	public static class Handler implements IMessageHandler<MessageSendUUIDToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendUUIDToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.worldpos.getWorld();
					if (!world.isAreaLoaded(message.worldpos.getPos(), 5)) {
						return;
					}
					TileEntity te = message.worldpos.getTileEntity();
					if (te instanceof TEProcessingCenter) {
						((TEProcessingCenter) te).setUUID(message.uuid);
					}
				}
			});
			return null;
		}
	}
}
