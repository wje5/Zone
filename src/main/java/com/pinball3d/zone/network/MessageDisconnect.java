package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDisconnect extends MessageSphinx {
	public MessageDisconnect() {

	}

	private MessageDisconnect(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageDisconnect newMessage(WorldPos pos, SerialNumber serialNumber) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("serial", serialNumber.writeToNBT(new NBTTagCompound()));
		return new MessageDisconnect(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getProcessingCenter().removeNeedNetwork(new SerialNumber(tag.getCompoundTag("serial")));
	}

	public static class Handler implements IMessageHandler<MessageDisconnect, IMessage> {
		@Override
		public IMessage onMessage(MessageDisconnect message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
