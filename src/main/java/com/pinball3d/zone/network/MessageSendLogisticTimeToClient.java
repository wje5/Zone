package com.pinball3d.zone.network;

import java.util.Stack;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.container.GuiContainerIOPanel;
import com.pinball3d.zone.sphinx.subscreen.SubscreenIOPanelRequest;
import com.pinball3d.zone.util.StorageWrapper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendLogisticTimeToClient implements IMessage {
	int time;
	StorageWrapper wrapper;

	public MessageSendLogisticTimeToClient() {

	}

	public MessageSendLogisticTimeToClient(int time, StorageWrapper wrapper) {
		this.time = time;
		this.wrapper = wrapper;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		time = buf.readInt();
		wrapper = new StorageWrapper(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(time);
		ByteBufUtils.writeTag(buf, wrapper.writeToNBT(new NBTTagCompound()));
	}

	public static class Handler implements IMessageHandler<MessageSendLogisticTimeToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendLogisticTimeToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerIOPanel) {
					GuiContainerIOPanel panel = (GuiContainerIOPanel) screen;
					Stack<Subscreen> stack = panel.getSubscreens();
					if (!stack.empty()) {
						Subscreen s = stack.get(0);
						if (s instanceof SubscreenIOPanelRequest) {
							((SubscreenIOPanelRequest) s).updateTime(message.time, message.wrapper);
						}
					}
				}
			});
			return null;
		}
	}
}
