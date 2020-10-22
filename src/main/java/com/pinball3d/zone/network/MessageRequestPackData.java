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

public class MessageRequestPackData implements IMessage {
	String name;
	int dim;
	WorldPos pos;

	public MessageRequestPackData() {

	}

	public MessageRequestPackData(EntityPlayer player, WorldPos pos) {
		name = player.getName();
		dim = player.dimension;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		dim = buf.readInt();
		pos = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(dim);
		pos.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageRequestPackData, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestPackData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dim);
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					TileEntity tileentity = message.pos.getTileEntity();
					if (tileentity instanceof TEProcessingCenter) {
						TEProcessingCenter te = (TEProcessingCenter) tileentity;
						te.sendPackDataToClient(player);
					}
				}
			});
			return null;
		}
	}
}
