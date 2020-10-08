package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.MapHandler;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendMapDataToClient implements IMessage {
	WorldPos pos;
	NBTTagCompound data;
	int[] lines;

	public MessageSendMapDataToClient() {

	}

	public MessageSendMapDataToClient(WorldPos pos, NBTTagCompound data, int[] lines) {
		this.pos = pos;
		this.data = data;
		this.lines = lines;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		data = ByteBufUtils.readTag(buf);
		int length = buf.readInt();
		lines = new int[length];
		for (int i = 0; i < length; i++) {
			lines[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		ByteBufUtils.writeTag(buf, data);
		buf.writeInt(lines.length);
		for (int i : lines) {
			buf.writeInt(i);
		}
	}

	public static class Handler implements IMessageHandler<MessageSendMapDataToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageSendMapDataToClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MapHandler.setData(message.pos, message.data, message.lines);
				}
			});
			return null;
		}
	}
}
