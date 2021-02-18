package com.pinball3d.zone.network;

import java.util.Collection;
import java.util.Iterator;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNewClass extends MessageSphinx {
	public MessageNewClass() {

	}

	private MessageNewClass(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageNewClass newMessage(EntityPlayer player, WorldPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		return new MessageNewClass(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		Collection<ClassifyGroup> c = te.getClassifyGroups().values();
		tag: for (int i = 0;; i++) {
			String s = "Unnamed" + (i > 0 ? i : "");
			Iterator<ClassifyGroup> it = c.iterator();
			while (it.hasNext()) {
				if (it.next().getName().equals(s)) {
					continue tag;
				}
			}
			te.addClassifyGroup(new ClassifyGroup(s));
			return;
		}
	}

	public static class Handler implements IMessageHandler<MessageNewClass, IMessage> {
		@Override
		public IMessage onMessage(MessageNewClass message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}