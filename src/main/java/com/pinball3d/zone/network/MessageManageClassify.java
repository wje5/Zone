package com.pinball3d.zone.network;

import java.util.Iterator;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageManageClassify extends MessageSphinx {
	public MessageManageClassify() {

	}

	private MessageManageClassify(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageManageClassify newMessage(EntityPlayer player, WorldPos pos, ClassifyGroup group) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("group", group.writeToNBT(new NBTTagCompound()));
		return new MessageManageClassify(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		ClassifyGroup group = new ClassifyGroup(tag.getCompoundTag("group"));
		Iterator<ClassifyGroup> it = te.getClassifyGroups().iterator();
		while (it.hasNext()) {
			ClassifyGroup e = it.next();
			if (e.getName().equals(group.getName())) {
				e.clear();
				e.addAll(group.getItems());
				return;
			}
		}
		te.getClassifyGroups().add(group);
	}

	public static class Handler implements IMessageHandler<MessageManageClassify, IMessage> {
		@Override
		public IMessage onMessage(MessageManageClassify message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
