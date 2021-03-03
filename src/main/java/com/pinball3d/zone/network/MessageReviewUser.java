package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.log.LogApprovePermission;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReviewUser extends MessageSphinxAdmin {
	public MessageReviewUser() {

	}

	private MessageReviewUser(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageReviewUser newMessage(WorldPos pos, UUID uuid) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("uuid", uuid);
		return new MessageReviewUser(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		UserData data = te.getUsers().get(tag.getUniqueId("uuid"));
		if (data != null) {
			data.reviewing = false;
		}
		te.fireLog(new LogApprovePermission(te.getNextLogId(), getPlayer(ctx), data.uuid, data.name));
	}

	public static class Handler implements IMessageHandler<MessageReviewUser, IMessage> {
		@Override
		public IMessage onMessage(MessageReviewUser message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
