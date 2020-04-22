package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageWizardData implements IMessage {
	WorldPos pos;
	String adminPassword, name, loginPassword;

	public MessageWizardData() {

	}

	public MessageWizardData(WorldPos pos, String adminPassword, String name, String loginPassword) {
		this.pos = pos;
		this.adminPassword = adminPassword;
		this.name = name;
		this.loginPassword = loginPassword;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		adminPassword = ByteBufUtils.readUTF8String(buf);
		name = ByteBufUtils.readUTF8String(buf);
		loginPassword = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, adminPassword);
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, loginPassword);
	}

	public static class Handler implements IMessageHandler<MessageWizardData, IMessage> {
		@Override
		public IMessage onMessage(MessageWizardData message, MessageContext ctx) {
			TEProcessingCenter te = (TEProcessingCenter) message.pos
					.getTileEntity(Minecraft.getMinecraft().getIntegratedServer());
			te.saveWizardData(message.adminPassword, message.name, message.loginPassword);
			return null;
		}
	}
}
