package com.pinball3d.zone.network;

import com.pinball3d.zone.instrument.ChannelManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageMidiToServer implements IMessage {
	int command, pitch, keystroke;

	public MessageMidiToServer() {

	}

	public MessageMidiToServer(int command, int pitch, int keystroke) {
		this.command = command;
		this.pitch = pitch;
		this.keystroke = keystroke;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		command = buf.readInt();
		pitch = buf.readInt();
		keystroke = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(command);
		buf.writeInt(pitch);
		buf.writeInt(keystroke);
	}

	public static class Handler implements IMessageHandler<MessageMidiToServer, IMessage> {
		@Override
		public IMessage onMessage(MessageMidiToServer message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			NetworkHandler.instance
					.sendToDimension(
							new MessageMidiToClient(player, message.command,
									ChannelManager.getInstance().getChannel(player), message.pitch, message.keystroke),
							player.dimension);
			return null;
		}
	}
}
