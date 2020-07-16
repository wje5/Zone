package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeName extends MessageSphinxAdmin {
	public MessageChangeName() {

	}

	public MessageChangeName(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	public static MessageChangeName newMessage(String password, WorldPos pos, String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		return new MessageChangeName(password, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getTileEntity().setName(tag.getString("name"));
	}

	public static class Handler implements IMessageHandler<MessageChangeName, IMessage> {
		@Override
		public IMessage onMessage(MessageChangeName message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
