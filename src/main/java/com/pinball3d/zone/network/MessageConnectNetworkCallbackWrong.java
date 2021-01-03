package com.pinball3d.zone.network;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.GuiContainerSphinxBase;
import com.pinball3d.zone.sphinx.subscreen.SubscreenConnectToNetwork;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectNetworkCallbackWrong implements IMessage {
	public MessageConnectNetworkCallbackWrong() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageConnectNetworkCallbackWrong, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectNetworkCallbackWrong message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerSphinxBase) {
					GuiContainerSphinxBase base = (GuiContainerSphinxBase) screen;
					if (base.getSubscreens().size() >= 2) {
						Subscreen s = base.getSubscreens().get(1);
						if (s instanceof SubscreenConnectToNetwork) {
							((SubscreenConnectToNetwork) s).setData(false);
						}
					}
				}
			});
			return null;
		}
	}
}
