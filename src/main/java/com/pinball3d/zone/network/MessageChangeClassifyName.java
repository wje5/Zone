package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeClassifyName extends MessageSphinx {
	public MessageChangeClassifyName() {

	}

	public MessageChangeClassifyName(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageChangeClassifyName newMessage(EntityPlayer player, WorldPos pos, int id, String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setString("name", name);
		return new MessageChangeClassifyName(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		ClassifyGroup g = getProcessingCenter().getClassifyGroups().get(tag.getInteger("id"));
		if (g != null) {
			g.setName(tag.getString("name"));
		}
	}

	public static class Handler implements IMessageHandler<MessageChangeClassifyName, IMessage> {
		@Override
		public IMessage onMessage(MessageChangeClassifyName message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
