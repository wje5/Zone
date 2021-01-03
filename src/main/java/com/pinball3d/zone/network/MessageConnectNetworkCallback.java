package com.pinball3d.zone.network;

import java.util.Stack;
import java.util.UUID;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.GuiContainerSphinxBase;
import com.pinball3d.zone.sphinx.subscreen.SubscreenConnectToNetwork;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectNetworkCallback implements IMessage {
	UUID uuid;
	WorldPos pos;
	String password;

	public MessageConnectNetworkCallback() {

	}

	public MessageConnectNetworkCallback(UUID uuid, WorldPos pos, String password) {
		this.uuid = uuid;
		this.pos = pos;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		pos = WorldPos.readFromByte(buf);
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		pos.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, password);
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
						if (subscreen instanceof SubscreenConnectToNetwork) {
							((SubscreenConnectToNetwork) subscreen).setData(true);
						}
						((SubscreenNetworkConfig) stack.get(0)).refresh();
					}
				}
			});
			return null;
		}
	}
}
