package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestMapData implements IMessage {
	String name;
	WorldPos pos;

	public MessageRequestMapData() {

	}

	public MessageRequestMapData(EntityPlayer player, WorldPos pos) {
		name = player.getName();
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		pos = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		pos.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageRequestMapData, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestMapData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.pos.getWorld();
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					TileEntity tileentity = message.pos.getTileEntity();
					if (tileentity instanceof TEProcessingCenter) {
						TEProcessingCenter te = (TEProcessingCenter) tileentity;
						te.sendMapDataToClient(player);
					}
				}
			});
			return null;
		}
	}
}
