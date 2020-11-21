package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenSphinxBase;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.SubscreenConnectToNetwork;
import com.pinball3d.zone.sphinx.SubscreenNetworkConfig;

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
				if (screen instanceof ScreenSphinxBase) {
					ScreenSphinxBase base = (ScreenSphinxBase) screen;
					if (!base.subscreens.empty()) {
						Subscreen s = base.subscreens.get(0);
						if (s instanceof SubscreenNetworkConfig) {
							if (!s.subscreens.empty()) {
								s = s.subscreens.get(0);
								if (s instanceof SubscreenConnectToNetwork) {
									((SubscreenConnectToNetwork) s).setData(false);
								}
							}
						}
					}
				}
			});
			return null;
		}
	}
}
