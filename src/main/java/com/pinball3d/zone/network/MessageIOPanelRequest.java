package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelRequest extends MessageSphinxNeedNetwork {
	public MessageIOPanelRequest() {

	}

	private MessageIOPanelRequest(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageIOPanelRequest newMessage(EntityPlayer player, WorldPos needNetwork, StorageWrapper req) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("req", req.writeToNBT(new NBTTagCompound()));
		return new MessageIOPanelRequest(player, needNetwork, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		SerialNumber serial = getProcessingCenter().getSerialNumberFromPos(pos);
		StorageWrapper wrapper = new StorageWrapper(tag.getCompoundTag("req"));
		int time = getProcessingCenter().requestItems(wrapper.copy(), pos, true);
//		getProcessingCenter().fireLog(new LogIOPanelRequest(getProcessingCenter().getNextLogId(), getPlayer(), serial,
//				new StorageWrapper((NBTTagCompound) tag.getTag("req")), time));//TODO
		getProcessingCenter().requestItems(wrapper, pos, false);
	}

	public static class Handler implements IMessageHandler<MessageIOPanelRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelRequest message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
