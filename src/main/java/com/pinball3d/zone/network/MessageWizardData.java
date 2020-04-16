package com.pinball3d.zone.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageWizardData implements IMessage {
	String adminPassword, name, loginPassword;

	public MessageWizardData(String adminPassword, String name, String loginPassword) {
		this.adminPassword = adminPassword;
		this.name = name;
		this.loginPassword = loginPassword;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		adminPassword = ByteBufUtils.readUTF8String(buf);
		name = ByteBufUtils.readUTF8String(buf);
		loginPassword = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, adminPassword);
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, loginPassword);
	}

	public static class Handler implements IMessageHandler<MessageWizardData, IMessage> {
		@Override
		public IMessage onMessage(MessageWizardData message, MessageContext ctx) {
			return null;
		}
	}
}
