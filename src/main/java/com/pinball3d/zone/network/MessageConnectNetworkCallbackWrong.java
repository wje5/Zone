package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenTerminal;
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
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						ScreenTerminal terminal = (ScreenTerminal) screen;
						if (!terminal.subscreens.empty()) {
							Subscreen s = terminal.subscreens.get(0);
							if (s instanceof SubscreenNetworkConfig) {
								if (!s.subscreens.empty()) {
									s = s.subscreens.get(0);
									if (s instanceof SubscreenConnectToNetwork) {
										((SubscreenConnectToNetwork) s).setData(false);
									}
								}
							}
						}
					} else if (screen instanceof ScreenNeedNetwork) {
						ScreenNeedNetwork n = (ScreenNeedNetwork) screen;
						if (!n.subscreens.empty()) {
							Subscreen s = n.subscreens.get(0);
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
				}
			});
			return null;
		}
	}
}
