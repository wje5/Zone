package com.pinball3d.zone.network;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeleteClassify extends MessageSphinx {
	public MessageDeleteClassify() {

	}

	public MessageDeleteClassify(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageDeleteClassify newMessage(EntityPlayer player, WorldPos pos, int id) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		return new MessageDeleteClassify(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getProcessingCenter().getClassifyGroups().remove(tag.getInteger("id"));
	}

	public static class Handler implements IMessageHandler<MessageDeleteClassify, IMessage> {
		@Override
		public IMessage onMessage(MessageDeleteClassify message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
