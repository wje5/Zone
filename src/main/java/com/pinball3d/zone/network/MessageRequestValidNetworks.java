package com.pinball3d.zone.network;

import java.util.List;

import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestValidNetworks implements IMessage {
	String name;
	WorldPos pos;

	public MessageRequestValidNetworks() {

	}

	public MessageRequestValidNetworks(EntityPlayer player, WorldPos pos) {
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

	public static class Handler implements IMessageHandler<MessageRequestValidNetworks, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestValidNetworks message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.pos.getWorld();
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					List<WorldPos> list = SphinxUtil.getValidNetworks(player.dimension, message.pos.getPos().getX(),
							message.pos.getPos().getY(), message.pos.getPos().getZ());
					NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(list), player);
				}
			});
			return null;
		}
	}
}
