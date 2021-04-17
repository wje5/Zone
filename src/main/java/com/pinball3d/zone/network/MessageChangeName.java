package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.log.LogChangeName;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeName extends MessageSphinxAdmin {
	public MessageChangeName() {

	}

	private MessageChangeName(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageChangeName newMessage(WorldPos pos, String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		return new MessageChangeName(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		String name = tag.getString("name");
		te.fireLog(new LogChangeName(te.getNextLogId(), name, getPlayer(ctx)));
		te.setName(name);
	}

	public static class Handler implements IMessageHandler<MessageChangeName, IMessage> {
		@Override
		public IMessage onMessage(MessageChangeName message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
