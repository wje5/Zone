package com.pinball3d.zone.network;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangePassword extends MessageSphinxAdmin {
	public MessageChangePassword() {

	}

	public MessageChangePassword(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	public static MessageChangePassword newMessage(String password, WorldPos pos, String newPassword) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("newPassword", newPassword);
		return new MessageChangePassword(password, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getTileEntity().setPassword(tag.getString("newPassword"));
	}

	public static class Handler implements IMessageHandler<MessageChangePassword, IMessage> {
		@Override
		public IMessage onMessage(MessageChangePassword message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
