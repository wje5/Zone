package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.log.LogNewOreDictionary;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNewOreDictionary extends MessageSphinx {
	public MessageNewOreDictionary() {

	}

	private MessageNewOreDictionary(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageNewOreDictionary newMessage(WorldPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		return new MessageNewOreDictionary(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		int id = te.newOreDictionary();
		te.fireLog(new LogNewOreDictionary(te.getNextLogId(), getPlayer(ctx), id));
		NetworkHandler.instance.sendTo(new MessageNewOreDictionaryCallback(id), (EntityPlayerMP) getPlayer(ctx));
	}

	public static class Handler implements IMessageHandler<MessageNewOreDictionary, IMessage> {
		@Override
		public IMessage onMessage(MessageNewOreDictionary message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
