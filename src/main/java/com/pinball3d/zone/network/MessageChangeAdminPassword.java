package com.pinball3d.zone.network;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeAdminPassword extends MessageSphinxAdmin {
	public MessageChangeAdminPassword() {

	}

	public MessageChangeAdminPassword(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	public static MessageChangeAdminPassword newMessage(String password, WorldPos pos, String newPassword) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("newPassword", newPassword);
		return new MessageChangeAdminPassword(password, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getTileEntity().setAdminPassword(tag.getString("newPassword"));
	}

	public static class Handler implements IMessageHandler<MessageChangeAdminPassword, IMessage> {
		@Override
		public IMessage onMessage(MessageChangeAdminPassword message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
