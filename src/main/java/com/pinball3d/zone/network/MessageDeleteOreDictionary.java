package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeleteOreDictionary extends MessageSphinx {
	public MessageDeleteOreDictionary() {

	}

	private MessageDeleteOreDictionary(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageDeleteOreDictionary newMessage(WorldPos pos, int id) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		return new MessageDeleteOreDictionary(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		int id = tag.getInteger("id");
		TEProcessingCenter te = getProcessingCenter();
		OreDictionaryData o = te.getOreDictionarys().get(id);
		te.getOreDictionarys().remove(id);
//		te.fireLog(new LogDeleteClassify(te.getNextLogId(), getPlayer(ctx), id, g.getName()));
	}

	public static class Handler implements IMessageHandler<MessageDeleteOreDictionary, IMessage> {
		@Override
		public IMessage onMessage(MessageDeleteOreDictionary message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
