package com.pinball3d.zone.network;

import java.util.List;

import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
					World world = message.pos.getWorld();
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					List<WorldPos> list = SphinxUtil.getValidNetworks(player.dimension, message.pos.getPos().getX(),
							message.pos.getPos().getY(), message.pos.getPos().getZ());
					NBTTagCompound tag = new NBTTagCompound();
					if (!message.isPlayer) {
						TileEntity tileentity = message.pos.getTileEntity();
						if (tileentity instanceof INeedNetwork) {
							WorldPos network = ((INeedNetwork) tileentity).getNetworkPos();
							if (network != null) {
								tag.setTag("connected", network.writeToNBT(new NBTTagCompound()));
							}
						}
					}
					NBTTagList taglist = new NBTTagList();
					list.forEach(e -> {
						TEProcessingCenter te = (TEProcessingCenter) e.getTileEntity();
						NBTTagCompound t = new NBTTagCompound();
						t.setString("name", te.getName());
						e.writeToNBT(t);
						taglist.appendTag(t);
					});
					tag.setTag("list", taglist);
					NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(tag), player);
				}
			});
			return null;
		}
	}
}
