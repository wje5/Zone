package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageManageOreDictionaryPriority extends MessageSphinx {
	public MessageManageOreDictionaryPriority() {

	}

	private MessageManageOreDictionaryPriority(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageManageOreDictionaryPriority newMessage(WorldPos pos, int id, OreDictionaryData data) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setTag("data", data.writeToNBT(new NBTTagCompound()));
		return new MessageManageOreDictionaryPriority(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		OreDictionaryData data = new OreDictionaryData(tag.getCompoundTag("data"));
		int id = tag.getInteger("id");
//		te.fireLog(new LogChangeOreDictionaryPriority(te.getNextLogId(), getPlayer(ctx), id, data.getName()));//TODO
		te.getOreDictionarys().put(id, data);
	}

	public static class Handler implements IMessageHandler<MessageManageOreDictionaryPriority, IMessage> {
		@Override
		public IMessage onMessage(MessageManageOreDictionaryPriority message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
