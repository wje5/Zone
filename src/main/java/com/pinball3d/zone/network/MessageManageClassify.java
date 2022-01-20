package com.pinball3d.zone.network;

import java.util.Iterator;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageManageClassify extends MessageSphinx {
	public MessageManageClassify() {

	}

	private MessageManageClassify(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageManageClassify newMessage(WorldPos pos, int id, ClassifyGroup group) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setTag("group", group.writeToNBT(new NBTTagCompound()));
		return new MessageManageClassify(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		ClassifyGroup group = new ClassifyGroup(tag.getCompoundTag("group"));
		int id = tag.getInteger("id");
		ClassifyGroup origin = te.getClassifyGroups().get(id);
		int intersection = 0;
		Iterator<ItemType> it = origin.getItems().get().iterator();
		while (it.hasNext()) {
			if (group.contains(it.next())) {
				intersection++;
			}
		}
		int added = group.getItems().get().size() - intersection;
		int removed = origin.getItems().get().size() - intersection;
//		te.fireLog(new LogManageClassify(te.getNextLogId(), getPlayer(ctx), id, group.getName(), removed, added));// TODO
		te.getClassifyGroups().put(id, group);
	}

	public static class Handler implements IMessageHandler<MessageManageClassify, IMessage> {
		@Override
		public IMessage onMessage(MessageManageClassify message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
