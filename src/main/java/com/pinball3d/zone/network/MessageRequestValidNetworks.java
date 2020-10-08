package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestValidNetworks implements IMessage {
	String name;
	WorldPos pos;
	boolean isPlayer;

	public MessageRequestValidNetworks() {

	}

	public MessageRequestValidNetworks(EntityPlayer player, WorldPos pos, boolean isPlayer) {
		name = player.getName();
		this.pos = pos;
		this.isPlayer = isPlayer;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		pos = WorldPos.readFromByte(buf);
		isPlayer = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		pos.writeToByte(buf);
		buf.writeBoolean(isPlayer);
	}

	public static class Handler implements IMessageHandler<MessageRequestValidNetworks, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestValidNetworks message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = message.pos.getWorld().getPlayerEntityByName(message.name);
					NBTTagCompound tag = SphinxUtil.getValidNetworkData(message.pos, player, message.isPlayer);
					NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(tag), (EntityPlayerMP) player);
				}
			});
			return null;
		}
	}
}
