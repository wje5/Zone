package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.instrument.ClientMidiHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageMidiToClient implements IMessage {
	UUID uuid;
	int command, channel, pitch, keystroke;

	public MessageMidiToClient() {

	}

	public MessageMidiToClient(EntityPlayer player, int command, int channel, int pitch, int keystroke) {
		uuid = player.getUniqueID();
		this.command = command;
		this.channel = channel;
		this.pitch = pitch;
		this.keystroke = keystroke;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		command = buf.readInt();
		channel = buf.readInt();
		pitch = buf.readInt();
		keystroke = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeInt(command);
		buf.writeInt(channel);
		buf.writeInt(pitch);
		buf.writeInt(keystroke);
	}

	public static class Handler implements IMessageHandler<MessageMidiToClient, IMessage> {
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(MessageMidiToClient message, MessageContext ctx) {
			if (!message.uuid.equals(Minecraft.getMinecraft().player.getUniqueID())) {
				ClientMidiHandler.onMessage(message.command, message.channel, message.pitch, message.keystroke);
			}
			return null;
		}
	}
}
