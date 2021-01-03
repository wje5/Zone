package com.pinball3d.zone.network;

import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNeedNetworkInfo implements IMessage {
	String name;
	WorldPos pos;

	public MessageRequestNeedNetworkInfo() {

	}

	public MessageRequestNeedNetworkInfo(EntityPlayer player, WorldPos pos) {
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

	public static class Handler implements IMessageHandler<MessageRequestNeedNetworkInfo, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestNeedNetworkInfo message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = message.pos.getWorld();
				EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
				TileEntity tileentity = message.pos.getTileEntity();
				if (tileentity instanceof INeedNetwork) {
					INeedNetwork te = (INeedNetwork) tileentity;
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("name", te.getName());
					tag.setInteger("state", te.getWorkingState().ordinal());
					NetworkHandler.instance.sendTo(new MessageSendNeedNetworkInfoToClient(message.pos, tag), player);
				}
			});
			return null;
		}
	}
}
