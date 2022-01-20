package com.pinball3d.zone.network;

import java.util.Map;
import java.util.UUID;

import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTransferAdmin extends MessageSphinxAdmin {
	public MessageTransferAdmin() {

	}

	private MessageTransferAdmin(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageTransferAdmin newMessage(WorldPos pos, UUID uuid) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("uuid", uuid);
		return new MessageTransferAdmin(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		UUID uuid = tag.getUniqueId("uuid");
		TEProcessingCenter te = getProcessingCenter();
		Map<UUID, UserData> map = te.getUsers();
		UserData data = map.get(uuid);
		UserData data2 = map.get(getPlayer(ctx).getUniqueID());
		if (data != null && data2 != null) {
			data2.admin = false;
			data.admin = true;
//			te.fireLog(new LogTransferAdmin(te.getNextLogId(), getPlayer(ctx), data.uuid, data.name));// TODO
		}
	}

	public static class Handler implements IMessageHandler<MessageTransferAdmin, IMessage> {
		@Override
		public IMessage onMessage(MessageTransferAdmin message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
