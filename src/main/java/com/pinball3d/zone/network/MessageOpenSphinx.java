package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenSphinx extends MessageSphinx {
	public MessageOpenSphinx() {

	}

	public MessageOpenSphinx(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getTileEntity().open();
	}

	public static class Handler implements IMessageHandler<MessageOpenSphinx, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenSphinx message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
