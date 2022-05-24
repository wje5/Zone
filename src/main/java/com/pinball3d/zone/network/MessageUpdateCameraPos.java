package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.elite.map.ServerChunkHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateCameraPos implements IMessage {
	private UUID uuid;
	private BlockPos pos;

	public MessageUpdateCameraPos() {

	}

	public MessageUpdateCameraPos(EntityPlayer player, BlockPos pos) {
		uuid = player.getUniqueID();
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(pos.toLong());
	}

	public static class Handler implements IMessageHandler<MessageUpdateCameraPos, IMessage> {
		@Override
		public IMessage onMessage(MessageUpdateCameraPos message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
						.getPlayerByUUID(message.uuid);
				if (player != null) {
					ServerChunkHelper.setCameraPos(message.uuid, message.pos);
				}
			});
			return null;
		}
	}
}
