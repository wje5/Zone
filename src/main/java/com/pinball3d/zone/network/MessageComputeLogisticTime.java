package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.INeedNetwork;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageComputeLogisticTime extends MessageSphinx {
	public MessageComputeLogisticTime() {

	}

	private MessageComputeLogisticTime(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageComputeLogisticTime newMessage(WorldPos network, WorldPos needNetwork,
			StorageWrapper wrapper) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("pos", needNetwork.writeToNBT(new NBTTagCompound()));
		tag.setTag("items", wrapper.writeToNBT(new NBTTagCompound()));
		return new MessageComputeLogisticTime(network, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		WorldPos p = new WorldPos(tag.getCompoundTag("pos"));
		TileEntity tileentity = p.getTileEntity();
		if (tileentity instanceof INeedNetwork) {
			StorageWrapper wrapper = new StorageWrapper(tag.getCompoundTag("items"));
			int time = getProcessingCenter().requestItems(wrapper.copy(), p, true);
			NetworkHandler.instance.sendTo(new MessageSendLogisticTimeToClient(time, wrapper),
					(EntityPlayerMP) getPlayer(ctx));
		}
	}

	public static class Handler implements IMessageHandler<MessageComputeLogisticTime, IMessage> {
		@Override
		public IMessage onMessage(MessageComputeLogisticTime message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
