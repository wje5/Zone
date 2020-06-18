package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendNetworkDataToTerminal implements IMessage {
	WorldPos worldpos;
	UUID uuid;

	public MessageSendNetworkDataToTerminal() {

	}

	public MessageSendNetworkDataToTerminal(WorldPos worldpos, UUID uuid) {
		this.worldpos = worldpos;
		this.uuid = uuid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		if (!buf.readBoolean()) {
			worldpos = WorldPos.readFromByte(buf);
		}
		uuid = new UUID(buf.readLong(), buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (worldpos == null) {
			buf.writeBoolean(true);
		} else {
			buf.writeBoolean(false);
			worldpos.writeToByte(buf);
		}
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}

	public static class Handler implements IMessageHandler<MessageSendNetworkDataToTerminal, IMessage> {
		@Override
		public IMessage onMessage(MessageSendNetworkDataToTerminal message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						((ScreenTerminal) screen).setWorldPos(message.worldpos, message.uuid);
					}
				}
			});
			return null;
		}
	}
}
