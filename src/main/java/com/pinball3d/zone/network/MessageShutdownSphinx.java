package com.pinball3d.zone.network;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageShutdownSphinx extends MessageSphinxAdmin {
	public MessageShutdownSphinx() {

	}

	private MessageShutdownSphinx(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageShutdownSphinx newMessage(EntityPlayer player, WorldPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		return new MessageShutdownSphinx(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getProcessingCenter().shutdown();
	}

	public static class Handler implements IMessageHandler<MessageShutdownSphinx, IMessage> {
		@Override
		public IMessage onMessage(MessageShutdownSphinx message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
