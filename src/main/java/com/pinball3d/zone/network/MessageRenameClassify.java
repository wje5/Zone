package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRenameClassify extends MessageSphinx {
	public MessageRenameClassify() {

	}

	private MessageRenameClassify(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageRenameClassify newMessage(WorldPos pos, int id, String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setString("name", name);
		return new MessageRenameClassify(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		int id = tag.getInteger("id");
		ClassifyGroup g = te.getClassifyGroups().get(id);
		String oldName = g.getName();
		String newName = tag.getString("name");
		if (g != null) {
			g.setName(newName);
		}
//		te.fireLog(new LogRenameClassify(te.getNextLogId(), getPlayer(ctx), id, oldName, newName));//TODO
	}

	public static class Handler implements IMessageHandler<MessageRenameClassify, IMessage> {
		@Override
		public IMessage onMessage(MessageRenameClassify message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
