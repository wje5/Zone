package com.pinball3d.zone.network;

import com.pinball3d.zone.inventory.ContainerIOPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateContainerIOPanel implements IMessage {
	int[] data;

	public MessageUpdateContainerIOPanel() {

	}

	public MessageUpdateContainerIOPanel(int[] data) {
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int count = buf.readInt();
		data = new int[count];
		for (int i = 0; i < count; i++) {
			data[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(data.length);
		for (int i : data) {
			buf.writeInt(i);
		}
	}

	public static class Handler implements IMessageHandler<MessageUpdateContainerIOPanel, IMessage> {
		@Override
		public IMessage onMessage(MessageUpdateContainerIOPanel message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				Container container = Minecraft.getMinecraft().player.openContainer;
				if (container instanceof ContainerIOPanel) {
					((ContainerIOPanel) container).setData(message.data);
				}
			});
			return null;
		}
	}
}
