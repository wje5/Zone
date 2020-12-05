package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.SubscreenMessageBox;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageErrorStorageFull implements IMessage {
	public MessageErrorStorageFull() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageErrorStorageFull, IMessage> {
		@Override
		public IMessage onMessage(MessageErrorStorageFull message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof IParent) {
					IParent s = (IParent) screen;
					s.putScreen(new SubscreenMessageBox(s, I18n.format("sphinx.warning"),
							I18n.format("sphinx.storage_full")));
				}
			});
			return null;
		}
	}
}