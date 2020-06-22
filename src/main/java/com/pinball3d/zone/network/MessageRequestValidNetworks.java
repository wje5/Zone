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
	int dim;

	public MessageRequestValidNetworks() {

	}

	public MessageRequestValidNetworks(EntityPlayer player) {
		name = player.getName();
		dim = player.dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		dim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(dim);
	}

	public static class Handler implements IMessageHandler<MessageRequestValidNetworks, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestValidNetworks message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dim);
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					List<WorldPos> list = SphinxUtil.getValidNetworks(player.dimension, player.posX, player.posY,
							player.posZ);
					NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(list), player);
				}
			});
			return null;
		}
	}
}
