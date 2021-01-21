package com.pinball3d.zone.network;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenSphinx extends MessageSphinxAdmin {
	public MessageOpenSphinx() {

	}

	public MessageOpenSphinx(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getProcessingCenter().open();
	}

	public static class Handler implements IMessageHandler<MessageOpenSphinx, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenSphinx message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
