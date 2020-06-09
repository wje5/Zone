package com.pinball3d.zone.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateNetworkDataToClient implements IMessage {
	NBTTagCompound nbt;

	public MessageUpdateNetworkDataToClient() {

	}

	public MessageUpdateNetworkDataToClient(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class Handler implements IMessageHandler<MessageUpdateNetworkDataToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageUpdateNetworkDataToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
//					World world = Minecraft.getMinecraft().world;
//					GlobalNetworkData.getData(world).readFromNBT(message.nbt);
//					GlStateManager.rotate(30.0F, 0, 1.0F, 0);
//					TODO
				}
			});
			return null;
		}
	}
}
