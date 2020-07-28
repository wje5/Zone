package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenSphinxController;
import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.SubscreenViewStorage;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendStorageToClient implements IMessage {
	StorageWrapper data;

	public MessageSendStorageToClient() {

	}

	public MessageSendStorageToClient(StorageWrapper wrapper) {
		data = wrapper;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = new StorageWrapper(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data.writeToNBT(new NBTTagCompound()));
	}

	public static class Handler implements IMessageHandler<MessageSendStorageToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendStorageToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						if (!((ScreenTerminal) screen).subscreens.empty()) {
							Subscreen subscreen = ((ScreenTerminal) screen).subscreens.get(0);
							if (subscreen instanceof SubscreenViewStorage) {
								((SubscreenViewStorage) subscreen).data = message.data;
							}
						}
					} else if (screen instanceof ScreenSphinxController) {
						if (!((ScreenSphinxController) screen).subscreens.empty()) {
							Subscreen subscreen = ((ScreenSphinxController) screen).subscreens.get(0);
							if (subscreen instanceof SubscreenViewStorage) {
								((SubscreenViewStorage) subscreen).data = message.data;
							}
						}
					}
				}
			});
			return null;
		}
	}
}
