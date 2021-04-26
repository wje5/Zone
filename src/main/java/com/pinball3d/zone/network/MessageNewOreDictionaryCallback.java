package com.pinball3d.zone.network;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxBase;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageOreDictionaryPriority;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNewOreDictionaryCallback implements IMessage {
	int id;

	public MessageNewOreDictionaryCallback() {

	}

	public MessageNewOreDictionaryCallback(int id) {
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<MessageNewOreDictionaryCallback, IMessage> {
		@Override
		public IMessage onMessage(MessageNewOreDictionaryCallback message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerSphinxBase) {
					((GuiContainerSphinxBase) screen).putScreen(
							new SubscreenManageOreDictionaryPriority((IHasSubscreen) screen, null, message.id));
				}
			});
			return null;
		}
	}
}
