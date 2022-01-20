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

public class MessageDeleteUser extends MessageSphinxAdmin {
	public MessageDeleteUser() {

	}

	private MessageDeleteUser(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageDeleteUser newMessage(WorldPos pos, UUID uuid) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("uuid", uuid);
		return new MessageDeleteUser(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		UUID uuid = tag.getUniqueId("uuid");
		Map<UUID, UserData> map = te.getUsers();
		UserData data = map.get(uuid);
		if (data != null) {
			map.remove(uuid);
			if (data.reviewing) {
//				te.fireLog(new LogDenyPermission(te.getNextLogId(), getPlayer(ctx), data.uuid, data.name));//TODO
			} else {
//				te.fireLog(new LogDeleteUser(te.getNextLogId(), getPlayer(ctx), data.uuid, data.name));//TODO
			}
		}
	}

	public static class Handler implements IMessageHandler<MessageDeleteUser, IMessage> {
		@Override
		public IMessage onMessage(MessageDeleteUser message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
