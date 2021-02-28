package com.pinball3d.zone.network;

import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeGravatar extends MessageSphinx {
	public MessageChangeGravatar() {

	}

	private MessageChangeGravatar(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageChangeGravatar newMessage(WorldPos pos, String email) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("email", email);
		return new MessageChangeGravatar(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		UserData data = getProcessingCenter().getUsers().get(getPlayer(ctx).getUniqueID());
		if (data != null) {
			data.email = tag.getString("email");
		}

	}

	public static class Handler implements IMessageHandler<MessageChangeGravatar, IMessage> {
		@Override
		public IMessage onMessage(MessageChangeGravatar message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
