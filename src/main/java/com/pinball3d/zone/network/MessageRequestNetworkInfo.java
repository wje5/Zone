package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

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

public class MessageRequestNetworkInfo implements IMessage {
	String name;
	WorldPos pos;

	public MessageRequestNetworkInfo() {

	}

	public MessageRequestNetworkInfo(EntityPlayer player, WorldPos pos) {
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

	public static class Handler implements IMessageHandler<MessageRequestNetworkInfo, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestNetworkInfo message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.pos.getWorld();
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					TileEntity tileentity = message.pos.getTileEntity();
					if (tileentity instanceof TEProcessingCenter) {
						TEProcessingCenter te = (TEProcessingCenter) tileentity;
						NBTTagCompound tag = new NBTTagCompound();
						tag.setString("name", te.getName());
						tag.setInteger("state", te.getWorkingState().ordinal());
						tag.setInteger("energy", te.getEnergy());
						NetworkHandler.instance.sendTo(new MessageSendNetworkInfoToClient(message.pos, tag), player);
					}
				}
			});
			return null;
		}
	}
}
