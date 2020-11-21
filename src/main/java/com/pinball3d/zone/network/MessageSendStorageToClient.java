package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ScreenSphinxAdvenced;
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
	int usedStorage, maxStorage;

	public MessageSendStorageToClient() {

	}

	public MessageSendStorageToClient(StorageWrapper wrapper, int usedStorage, int maxStorage) {
		data = wrapper;
		this.usedStorage = usedStorage;
		this.maxStorage = maxStorage;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = new StorageWrapper(ByteBufUtils.readTag(buf));
		usedStorage = buf.readInt();
		maxStorage = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data.writeToNBT(new NBTTagCompound()));
		buf.writeInt(usedStorage);
		buf.writeInt(maxStorage);
	}

	public static class Handler implements IMessageHandler<MessageSendStorageToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendStorageToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof ScreenSphinxAdvenced) {
					ScreenSphinxAdvenced s = (ScreenSphinxAdvenced) screen;
					if (!(s.subscreens.empty())) {
						Subscreen subscreen = s.subscreens.get(0);
						if (subscreen instanceof SubscreenViewStorage) {
							((SubscreenViewStorage) subscreen).setData(message.data, message.usedStorage,
									message.maxStorage);
						}
					}
				}
			});
			return null;
		}
	}
}
