package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.StorageWrapper;
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

public class MessageComputeLogisticTime implements IMessage {
	String name;
	WorldPos needNetwork;
	StorageWrapper wrapper;

	public MessageComputeLogisticTime() {

	}

	public MessageComputeLogisticTime(EntityPlayer player, WorldPos needNetwork, StorageWrapper wrapper) {
		name = player.getName();
		this.needNetwork = needNetwork;
		this.wrapper = wrapper;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		needNetwork = WorldPos.readFromByte(buf);
		wrapper = new StorageWrapper(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		needNetwork.writeToByte(buf);
		ByteBufUtils.writeTag(buf, wrapper.writeToNBT(new NBTTagCompound()));
	}

	public static class Handler implements IMessageHandler<MessageComputeLogisticTime, IMessage> {
		@Override
		public IMessage onMessage(MessageComputeLogisticTime message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = message.needNetwork.getWorld();
				EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
				TileEntity te = message.needNetwork.getTileEntity();
				if (te instanceof INeedNetwork) {
					WorldPos pos = GlobalNetworkData.getPos(((INeedNetwork) te).getNetwork());
					if (pos != null) {
						TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
						int time = pc.requestItems(message.wrapper.copy(), message.needNetwork, true);
						NetworkHandler.instance.sendTo(new MessageSendLogisticTimeToClient(time, message.wrapper),
								player);
					}
				}
			});
			return null;
		}
	}
}
