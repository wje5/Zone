package com.pinball3d.zone.network;

import java.util.Stack;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxBase;
import com.pinball3d.zone.sphinx.subscreen.SubscreenConnectToNetworkBox;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectNetworkCallback implements IMessage {
	boolean flag;

	public MessageConnectNetworkCallback() {

	}

	public MessageConnectNetworkCallback(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(flag);
	}

	public static class Handler implements IMessageHandler<MessageConnectNetworkCallback, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectNetworkCallback message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerSphinxBase) {
					GuiContainerSphinxBase terminal = (GuiContainerSphinxBase) screen;
					Stack<Subscreen> stack = terminal.getSubscreens();
					if (stack.size() >= 2) {
						Subscreen subscreen = stack.get(1);
						if (subscreen instanceof SubscreenConnectToNetworkBox) {
							((SubscreenConnectToNetworkBox) subscreen).setData(message.flag);
						}
						((SubscreenNetworkConfig) stack.get(0)).refresh();
					}
				}
			});
			return null;
		}
	}
}
